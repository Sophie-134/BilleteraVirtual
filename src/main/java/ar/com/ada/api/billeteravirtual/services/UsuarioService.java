package ar.com.ada.api.billeteravirtual.services;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.springframework.stereotype.Service;

import ar.com.ada.api.billeteravirtual.entities.Persona;
import ar.com.ada.api.billeteravirtual.entities.Usuario;
import ar.com.ada.api.billeteravirtual.repositories.UsuarioRepository;

@Service
public class UsuarioService {
    @Autowired
    UsuarioRepository repo;
    @Autowired
    PersonaService personaService;

	public Usuario buscarPorUsername(String username) {
		return null;
	}

	public void login(String username, String password) {
	}
    
    //1.3:crear billetera(setear persona, crear cuenta en moneda del pais de la persona)
    //2: nuevo metodo agregarNuevaCuenta
    //3: metodo iniciarSesion
    //3.1: recibir el username, password
    //3.2: validar los datos, devolviendo true/false
 
   

    //1: Metodo crearUsuario
    //1.1:crear persona(setear usuario)
    //1.2:crear un usuario
    public Usuario crearUsuario(String fullName, int country, int identificationType, String identification, Date birthDate, String email, String password){
      Persona persona = new Persona();
      persona.setNombre(fullName);
      persona.setPaisId(country);
      persona.setTipoDocumentoId(identificationType);;
      persona.setDocumento(identification);
      persona.setFechaNacimiento(birthDate);

      Usuario usuario = new Usuario();
      usuario.setUsername(email);
      usuario.setEmail(email);
      usuario.setPassword(password);

      persona.setUsuario(usuario);
      personaService.grabar(persona);
      
        return usuario; 
    }
}