package ar.com.ada.api.billeteravirtual.services;

import java.math.BigDecimal;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;

import org.springframework.stereotype.Service;

import ar.com.ada.api.billeteravirtual.entities.Billetera;
import ar.com.ada.api.billeteravirtual.entities.Cuenta;
import ar.com.ada.api.billeteravirtual.entities.Persona;
import ar.com.ada.api.billeteravirtual.entities.Usuario;
import ar.com.ada.api.billeteravirtual.repositories.UsuarioRepository;
import ar.com.ada.api.billeteravirtual.security.Crypto;
import ar.com.ada.api.billeteravirtual.sistema.comm.EmailService;

@Service
public class UsuarioService {
  @Autowired
  UsuarioRepository repo;
  @Autowired
  PersonaService personaService;
  @Autowired
  BilleteraService billeteraService;
  @Autowired
  EmailService emailService;

  public Usuario buscarPorUsername(String username) {
    return repo.findByUsername(username);
  }

  public void login(String username, String password) {
    /**
     * Metodo IniciarSesion recibe usuario y contraseña validar usuario y contraseña
     */

    Usuario u = buscarPorUsername(username);

    if (u == null || !u.getPassword().equals(Crypto.encrypt(password, u.getUsername()))) {

      throw new BadCredentialsException("Usuario o contraseña invalida");
    }
  }

  // 1.3:crear billetera(setear persona, crear cuenta en moneda del pais de la
  // persona)
  // 2: nuevo metodo agregarNuevaCuenta
  // 3: metodo iniciarSesion
  // 3.1: recibir el username, password
  // 3.2: validar los datos, devolviendo true/false

  // 1: Metodo crearUsuario
  // 1.1:crear persona(setear usuario)
  // 1.2:crear un usuario
  public Usuario crearUsuario(String nombre, int pais, int tipoDocumento, String documento, Date fechaNacimiento,
      String email, String password) {
        
    Persona persona = new Persona();
    persona.setNombre(nombre);
    persona.setPaisId(pais);
    persona.setTipoDocumentoId(tipoDocumento);
    persona.setDocumento(documento);
    persona.setFechaNacimiento(fechaNacimiento);

    Usuario usuario = new Usuario();
    usuario.setUsername(email);
    usuario.setEmail(email);
    usuario.setPassword(Crypto.encrypt(password, email));

    persona.setUsuario(usuario);
    personaService.grabar(persona);

    Billetera billetera = new Billetera();

    Cuenta pesos = new Cuenta();
    pesos.setSaldo(new BigDecimal(0));
    pesos.setMoneda("ARS");
    billetera.agregarCuenta(pesos);

    Cuenta dolares = new Cuenta();
    dolares.setSaldo(new BigDecimal(0));
    dolares.setMoneda("USD");
    billetera.agregarCuenta(dolares);

    persona.setBilletera(billetera);
    billeteraService.grabarBilletera(billetera);

    billeteraService.cargarSaldo(new BigDecimal(500), "ARS", billetera, "Regalo", "Regalo de bienvenida");

    emailService.SendEmail(usuario.getEmail(), "Bienvenide a la Billetera Virtual", "Has recibido 500$ de regalo por su registro");
    return usuario;
  }

  public Usuario buscarPorEmail(String email) {
    return repo.findByEmail(email);
  }

}