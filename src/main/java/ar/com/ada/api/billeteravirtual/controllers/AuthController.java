package ar.com.ada.api.billeteravirtual.controllers;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import ar.com.ada.api.billeteravirtual.models.request.RegistrationRequest;
import ar.com.ada.api.billeteravirtual.models.response.ErrorResponseItem;
import ar.com.ada.api.billeteravirtual.models.response.JwtResponse;
import ar.com.ada.api.billeteravirtual.models.response.LoginResponse;
import ar.com.ada.api.billeteravirtual.models.response.RegistrationResponse;
import ar.com.ada.api.billeteravirtual.security.jwt.JWTTokenUtil;
import ar.com.ada.api.billeteravirtual.services.JWTUserDetailsService;
import ar.com.ada.api.billeteravirtual.services.UsuarioService;
import ar.com.ada.api.billeteravirtual.entities.Usuario;
import ar.com.ada.api.billeteravirtual.models.request.LoginRequest;;

/**
 * AuthController
 */
@RestController
public class AuthController {

    @Autowired
    UsuarioService usuarioService;

    /*
     * @Autowired private AuthenticationManager authenticationManager;
     */
    @Autowired
    private JWTTokenUtil jwtTokenUtil;

    @Autowired
    private JWTUserDetailsService userDetailsService;
    //Auth : authentication ->
    @PostMapping("auth/register")
    public ResponseEntity<RegistrationResponse> postRegisterUser(@Valid @RequestBody RegistrationRequest req, BindingResult results) {
        RegistrationResponse r = new RegistrationResponse();

        if(results.hasErrors()){
            //Pongo que el response ahora es false
            r.isOk = false;
            r.message = "Errores en la registracion";
            results.getFieldErrors().stream().forEach(e -> {
                //Recorro cada error (ahora particularmente los fields errors) y los voy agregando a la lista
                r.errors.add(new ErrorResponseItem(e.getField(), e.getDefaultMessage()));
        });
        return ResponseEntity.badRequest().body(r);
        }

        // aca creamos la persona y el usuario a traves del service.
        //Insertar codigo aqui
        Usuario usuario = usuarioService.crearUsuario(req.fullName, req.country, req.identificationType, req.identification, req.birthDate, req.email, req.password);
       //tmb se puede hacer Usuario usuario = usuarioService.crearUsuario(req.fullName, req.cou, blabla);
        r.isOk = true;
        r.message = "Te registraste con exitoooo!!!!!!!";
        r.userId = usuario.getUsuarioId(); // <-- AQUI ponemos el numerito de id para darle a front!
        return ResponseEntity.ok(r);

    }

    @PostMapping("auth/login") // probando nuestro login
    public ResponseEntity<?> createAuthenticationToken(@RequestBody LoginRequest authenticationRequest)
            throws Exception {

        usuarioService.login(authenticationRequest.username, authenticationRequest.password);

        UserDetails userDetails = userDetailsService.loadUserByUsername(authenticationRequest.username);

        String token = jwtTokenUtil.generateToken(userDetails);

        //return ResponseEntity.ok(new JwtResponse(token));

        //Cambio para que devuelva el full perfil
        Usuario u = usuarioService.buscarPorUsername(authenticationRequest.username);

        LoginResponse r = new LoginResponse();
        r.id = u.getUsuarioId();
        r.billeteraId = u.getPersona().getBilletera().getBilleteraId();
        r.username = authenticationRequest.username;
        r.email = u.getEmail();
        r.token = token;

        return ResponseEntity.ok(r);
        

    }

}