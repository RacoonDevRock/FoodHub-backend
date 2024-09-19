package com.project.FoodHub.config.service;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.project.FoodHub.config.util.JwtUtils;
import com.project.FoodHub.dto.*;
import com.project.FoodHub.entity.Creador;
import com.project.FoodHub.enumeration.Rol;
import com.project.FoodHub.exception.*;
import com.project.FoodHub.service.IColegiadoService;
import com.project.FoodHub.service.ICreadorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class IUserDetailService implements UserDetailsService {
    private final ICreadorService creadorService;
    private final IColegiadoService colegiadoService;
    private final JwtUtils jwtUtils;
    private final PasswordEncoder passwordEncoder;
    private final JavaMailSender javaMailSender;

    @Value("${spring.email.sender.user}")
    private String mailOrigin;

    @Value("${frontUrl}")
    private String frontUrl;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Creador creador = creadorService.obtenerCreadorPorEmail(username);
        return new User(creador.getCorreoElectronico(), creador.getContrasenia(), true, true, true, true, AuthorityUtils.createAuthorityList(creador.getRole().name()));
    }

    public AuthResponse iniciarSesion(AuthRequest authRequest) throws IncorrectCredentials {
        String identificador = authRequest.getIdentificador();
        String contrasenia = authRequest.getContrasenia();

        Creador creador;

        if (identificador.contains("@")) {
            creador = creadorService.obtenerCreadorPorEmail(identificador);
        } else {
            creador = creadorService.obtenerCreadorPorIdentificador(identificador);
        }

        Authentication authentication = authenticate(identificador, contrasenia);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        if (!creador.isEnabled()) {
            throw new CuentaNoConfirmadaException("Cuenta no confirmada");
        }

        String accessToken = jwtUtils.createToken(authentication);
        return new AuthResponse(accessToken);
    }

    private Authentication authenticate(String email, String password) throws IncorrectCredentials {
        UserDetails userDetails = loadUserByUsername(email);

        if (userDetails == null || !passwordEncoder.matches(password, userDetails.getPassword())) {
            throw new IncorrectCredentials("Invalid email or password");
        }

        return new UsernamePasswordAuthenticationToken(email, userDetails.getPassword(), userDetails.getAuthorities());
    }

    @Transactional
    public ConfirmacionResponse registrar(CreadorRequest request) {
        if (!colegiadoService.validarColegiado(request.getNombre(), request.getApellidoPaterno(), request.getApellidoMaterno(), request.getCodigoColegiatura())) {
            throw new ColegiadoNoValidoException("No se pudo validar el colegiado. Verifica los datos proporcionados.");
        }

        if (creadorService.verificarCorreoRegistrado(request.getCorreoElectronico()).isPresent()) {
            throw new CorreoConfirmadoException("El correo electrónico ya está registrado.");
        }

        if (!colegiadoService.isCuentaConfirmada(request.getCodigoColegiatura())) {
            throw new CodigoConfirmadoException("Código de colegiado ya registrado");
        }

        colegiadoService.confirmarCuenta(request.getCodigoColegiatura());

        Creador creador = new Creador(
                request.getNombre(),
                request.getApellidoPaterno(),
                request.getApellidoMaterno(),
                request.getCorreoElectronico(),
                request.getContrasenia(),
                request.getCodigoColegiatura()
        );

        creadorService.guardarCreador(creador);

        crearCuenta(creador);

        return new ConfirmacionResponse("Creación de cuenta exitosa", "success");
    }

    public void crearCuenta(Creador creador) {
        creadorService.obtenerCreadorPorEmail(creador.getCorreoElectronico());

        creador.setContrasenia(passwordEncoder.encode(creador.getContrasenia()));
        creador.setRole(Rol.USER);

        Creador creadorCreated = creadorService.guardarCreador(creador);

        SecurityContext securityContext = SecurityContextHolder.getContext();
        Authentication authentication =
                new UsernamePasswordAuthenticationToken(creadorCreated.getCorreoElectronico(),
                        creadorCreated.getContrasenia(),
                        AuthorityUtils.createAuthorityList(creadorCreated.getRole().name()));

        String accessToken = jwtUtils.createToken(authentication);

        sendSimpleMessage(
                creadorCreated.getCorreoElectronico(),
                "Estimado/a " + creador.getNombre() + ",\n\nGracias por registrarte en nuestra plataforma 'FoodHub'. Por favor, haz clic en el siguiente enlace para confirmar tu cuenta:\n\n" + frontUrl + "/verificar/" + accessToken + "\n\nSaludos,\ny disfruta de una nueva experiencia.");

    }

    private void sendSimpleMessage(String to, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(mailOrigin);
        message.setTo(to);
        message.setSubject("Account Verification - FoodHub");
        message.setText(text);
        javaMailSender.send(message);
    }

    public MessageResponse confirmAccount(String token) {

        DecodedJWT decodedJWT = jwtUtils.validateToken(token);
        String username = jwtUtils.extractUsername(decodedJWT);

        Creador creador = creadorService.obtenerCreadorPorEmail(username);
        if (creador == null) throw new CreadorNoEncontradoException("El usuario no existe.");

        creador.setEnabled(true);
        creadorService.guardarCreador(creador);

        return new MessageResponse("Cuenta confirmada exitosamente para " + username);
    }
}
