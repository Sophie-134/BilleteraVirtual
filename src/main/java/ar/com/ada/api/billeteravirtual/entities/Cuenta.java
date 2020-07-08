package ar.com.ada.api.billeteravirtual.entities;

import java.math.BigDecimal;
import java.util.*;

import javax.persistence.*;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import ar.com.ada.api.billeteravirtual.entities.Transaccion.TipoTransaccionEnum;

@Entity
@Table(name = "cuenta")
public class Cuenta {
    @Id
    @Column(name = "cuenta_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer cuentaId;
    private BigDecimal saldo;
    private String moneda;
    @ManyToOne
    @JoinColumn(name = "billetera_id", referencedColumnName = "billetera_id")
    private Billetera billetera;

    @OneToMany(mappedBy = "cuenta", cascade = CascadeType.ALL)
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<Transaccion> transacciones = new ArrayList<>();

    public Integer getCuentaId() {
        return cuentaId;
    }

    public void setCuentaId(Integer cuentaId) {
        this.cuentaId = cuentaId;
    }

    public BigDecimal getSaldo() {
        return saldo;
    }

    public void setSaldo(BigDecimal saldo) {
        this.saldo = saldo;
    }

    public String getMoneda() {
        return moneda;
    }

    public void setMoneda(String moneda) {
        this.moneda = moneda;
    }

    public Billetera getBilletera() {
        return billetera;
    }

    public void setBilletera(Billetera billetera) {
        this.billetera = billetera;
        // this.billetera.getCuentas().add(this);
    }

    public List<Transaccion> getTransacciones() {
        return transacciones;
    }

    public void setTransacciones(List<Transaccion> transacciones) {
        this.transacciones = transacciones;
    }

    // la relacion bidireccional se hace a travez de un metodo(agregarAlgo) q lo
    // agrega a la lista
    public void agregarTransaccion(Transaccion transaccion) {
        this.transacciones.add(transaccion);
        transaccion.setCuenta(this);

        BigDecimal saldoActual = this.getSaldo();
        BigDecimal importe = transaccion.getImporte();
        BigDecimal saldoSumado;

        if (transaccion.getTipoOperacion() == TipoTransaccionEnum.ENTRANTE) {
            saldoSumado = saldoActual.add(importe);
        } else {
            saldoSumado = saldoActual.subtract(importe);
        }
        this.setSaldo(saldoSumado);

    }

    public Transaccion generarTransaccion(String conceptoOperacion, String detalle, BigDecimal importe,
            TipoTransaccionEnum tipoOperacion) {

        Transaccion transaccion = new Transaccion();

        transaccion.setFecha(new Date());
        transaccion.setEstadoId(2); // 2=aprobado 0=pendiente -1=rechazada
        transaccion.setImporte(importe);
        transaccion.setMoneda(moneda);
        transaccion.setTipoOperacion(tipoOperacion); // 1=entrada, 0 =salida
        transaccion.setConceptoOperacion(conceptoOperacion);
        transaccion.setDetalle(detalle);

        if (transaccion.getTipoOperacion() == TipoTransaccionEnum.ENTRANTE) {// si es de entrada

            transaccion.setaUsuarioId(billetera.getPersona().getUsuario().getUsuarioId());
            transaccion.setaCuentaId(this.getCuentaId());
        } else { // de salida
            transaccion.setDeUsuarioId(billetera.getPersona().getUsuario().getUsuarioId());
            transaccion.setDeCuentaId(this.getCuentaId());
        }
        return transaccion;
    }
    // opsion 2(nunca me funciono...):
    /*
     * 
     * if (transaccion.getTipoOperacion()== TipoTransaccionEnum.ENTRANTE) {
     * 
     * BigDecimal saldoSumado = saldoActual.add(saldo); this.setSaldo(saldoSumado);
     * } else { BigDecimal saldoSumado = saldoActual.subtract(saldo);
     * this.setSaldo(saldoSumado); }
     */
}