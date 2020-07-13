package ar.com.ada.api.billeteravirtual.services;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ar.com.ada.api.billeteravirtual.entities.*;
import ar.com.ada.api.billeteravirtual.entities.Transaccion.ResultadoTransaccionEnum;
import ar.com.ada.api.billeteravirtual.entities.Transaccion.TipoTransaccionEnum;
import ar.com.ada.api.billeteravirtual.repositories.BilleteraRepository;
import ar.com.ada.api.billeteravirtual.sistema.comm.EmailService;

@Service
public class BilleteraService {
    @Autowired
    BilleteraRepository repo;
    @Autowired
    UsuarioService usuarioService;
    @Autowired
  EmailService emailService;

    public void grabarBilletera(Billetera billetera) {
        repo.save(billetera);
    }

    // 1: metodo cargarSaldo
    // 1.1: recibir un importe
    // 1.2: buscar la billetera por id
    // 1.3: buscar la cuenta por la moneda(no hay 2 cuentas en usd)
    // 1.4:crear transaccion
    // 1.5: actualizar el saldo de la billetera

    public void cargarSaldo(BigDecimal saldo, String moneda, Integer billeteraId, String conceptoOperacion,
            String detalle) {

        Billetera billetera = this.buscarBilleteraPorId(billeteraId);

        cargarSaldo(saldo, moneda, billetera, conceptoOperacion, detalle);

        emailService.SendEmail(billetera.getPersona().getUsuario().getEmail(), "Su Billetera Virtual", "Has realizado una carga de "+ saldo +" "+ moneda);
        // ??? this.grabarBilletera(billetera);
    }

    public void cargarSaldo(BigDecimal saldo, String moneda, Billetera billetera, String conceptoOperacion,
            String detalle) {

        Cuenta cuenta = billetera.getCuenta(moneda);

        Transaccion transaccion = new Transaccion();
        transaccion.setFecha(new Date());
        transaccion.setEstadoId(2); // 2=aprobado 0=pendiente -1=rechazada
        transaccion.setImporte(saldo);
        transaccion.setMoneda(moneda);
        transaccion.setTipoOperacion(TipoTransaccionEnum.ENTRANTE); // 1=entrada, 0 =salida
        transaccion.setConceptoOperacion(conceptoOperacion);
        transaccion.setDetalle(detalle);
        transaccion.setDeUsuarioId(billetera.getPersona().getUsuario().getUsuarioId());
        transaccion.setaUsuarioId(billetera.getPersona().getUsuario().getUsuarioId());
        transaccion.setDeCuentaId(cuenta.getCuentaId());
        transaccion.setaCuentaId(cuenta.getCuentaId());

        cuenta.agregarTransaccion(transaccion);

        this.grabarBilletera(billetera);
    }

    // 3: metodo consultarSaldo
    // 3.1: recibe id de billetera y la moneda de cuenta
    public BigDecimal consultarSaldo(Integer billeteraId, String moneda) {
        Billetera billetera = repo.findByBilleteraId(billeteraId);
        Cuenta cuenta = billetera.getCuenta(moneda);
        return cuenta.getSaldo();
    }

    public Billetera buscarBilleteraPorId(Integer id) {
        return repo.findByBilleteraId(id);

    }

    // 2: metodo enviar saldo
    // 2.1: recibir importe(recibir billetera de origen y otra de destino)
    // 2.2: buscar cuenta por moneda
    // 2.3: actualizar saldo de la cuenta origen y destino
    // 2.4: generar 2 transacciones
    public ResultadoTransaccionEnum enviarSaldo(BigDecimal importe, String moneda, Integer billeteraOrigenId,
            Integer billeteraDestinoId, String conceptoOperacion, String detalle) {

        if (importe.compareTo(new BigDecimal(0)) == -1)
            return ResultadoTransaccionEnum.ERROR_IMPORTE_NEGATIVO;

        Billetera billeteraSal = this.buscarBilleteraPorId(billeteraOrigenId);
        if (billeteraSal == null)
            return ResultadoTransaccionEnum.BILLETERA_ORIGEN_NO_ENCANTRADA;

        Billetera billeteraEnt = this.buscarBilleteraPorId(billeteraDestinoId);
        if (billeteraEnt == null)
            return ResultadoTransaccionEnum.BILLETERA_DESTINO_NO_ENCONTRADA;

        Cuenta cuentaSaliente = billeteraSal.getCuenta(moneda);
        if (cuentaSaliente == null)
            return ResultadoTransaccionEnum.CUENTA_ORIGEN_INEXISTENTE;

        Cuenta cuentaEntrante = billeteraEnt.getCuenta(moneda);
        if (cuentaEntrante == null)
            return ResultadoTransaccionEnum.CUENTA_DESTINO_INEXISTENTE;

        if (cuentaSaliente.getSaldo().compareTo(importe) == -1)
            return ResultadoTransaccionEnum.SALDO_INSUFICIENTE;

        Transaccion tSaliente = cuentaSaliente.generarTransaccion(conceptoOperacion, detalle, importe,
                TipoTransaccionEnum.SALIENTE);
        Transaccion tEntrante = new Transaccion();

        tSaliente.setaCuentaId(cuentaEntrante.getCuentaId());
        tSaliente.setaUsuarioId(billeteraEnt.getPersona().getUsuario().getUsuarioId());

        tEntrante = cuentaEntrante.generarTransaccion(conceptoOperacion, detalle, importe,
                TipoTransaccionEnum.ENTRANTE);
        tEntrante.setDeCuentaId(cuentaSaliente.getCuentaId());
        tEntrante.setDeUsuarioId(billeteraSal.getPersona().getUsuario().getUsuarioId());

        cuentaSaliente.agregarTransaccion(tSaliente);
        cuentaEntrante.agregarTransaccion(tEntrante);

        this.grabarBilletera(billeteraSal);
        emailService.SendEmail(billeteraSal.getPersona().getUsuario().getEmail(), "Su Billetera Virtual te informa", "Has realizado el envio de "+ importe +" "+ moneda + " a "+ billeteraEnt.getPersona().getUsuario().getEmail()+ " con exito.");
        this.grabarBilletera(billeteraEnt);
        emailService.SendEmail(billeteraEnt.getPersona().getUsuario().getEmail(), "Su Billetera Virtual te informa", "Has recibido "+ importe +" "+ moneda + " de " + billeteraSal.getPersona().getUsuario().getEmail());

        return ResultadoTransaccionEnum.INICIADA;
    }

    public ResultadoTransaccionEnum enviarSaldo(BigDecimal importe, String moneda, Integer billeteraOrigenId,
            String email, String concepto, String detalle) {
        Usuario usuarioDestino = usuarioService.buscarPorEmail(email);

        if (usuarioDestino == null)
            return ResultadoTransaccionEnum.EMAIL_DESTINO_INEXISTENTE;
        return this.enviarSaldo(importe, moneda, billeteraOrigenId,
                usuarioDestino.getPersona().getBilletera().getBilleteraId(), concepto, detalle);
    }

    /*
     * public void enviarSaldo(BigDecimal importe, String moneda, String email,
     * Integer billeteraOrigenId, String concepto, String detalle) {
     * 
     * Usuario usuarioDestino = usuarioService.buscarPorEmail(email);
     * this.enviarSaldo(importe, moneda, billeteraOrigenId,
     * usuarioDestino.getPersona().getBilletera().getBilleteraId(), concepto,
     * detalle);
     * 
     * }
     */

    public List<Transaccion> buscarTransacciones(Integer billeteraId, String moneda) {
            Billetera billetera = repo.findByBilleteraId(billeteraId);
            Cuenta cuenta = billetera.getCuenta(moneda);
            return cuenta.getTransacciones();
    }

}