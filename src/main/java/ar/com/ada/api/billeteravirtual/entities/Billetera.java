package ar.com.ada.api.billeteravirtual.entities;

import java.util.*;

import javax.persistence.*;


@Entity
@Table(name = "billetera")
public class Billetera {

    @Id
    @Column(name = "billetera_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer billeraId;
    @OneToMany
    @JoinColumn(name = "persona_id", referencedColumnName = "persona_id")
    private Persona persona;
//ese billetera es el atributo billetera de la clase Billetera, q esta en la clase Persona
    @OneToMany(mappedBy ="billetera", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Cuenta> cuentas = new ArrayList<>();

    public Integer getBilleraId() {
        return billeraId;
    }

    public void setBilleraId(Integer billeraId) {
        this.billeraId = billeraId;
    }

    public Persona getPersona() {
        return persona;
    }

    public void setPersona(Persona persona) {
        this.persona = persona;
    }

    public List<Cuenta> getCuentas() {
        return cuentas;
    }

    public void setCuentas(List<Cuenta> cuentas) {
        this.cuentas = cuentas;
        
    }
    public void agregarCuenta(Cuenta cuenta){
        this.cuentas.add(cuenta);
    }
}