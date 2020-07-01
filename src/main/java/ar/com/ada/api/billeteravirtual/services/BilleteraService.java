package ar.com.ada.api.billeteravirtual.services;

import java.math.BigDecimal;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ar.com.ada.api.billeteravirtual.entities.Billetera;
import ar.com.ada.api.billeteravirtual.entities.Cuenta;
import ar.com.ada.api.billeteravirtual.entities.Persona;
import ar.com.ada.api.billeteravirtual.entities.Transaccion;
import ar.com.ada.api.billeteravirtual.repositories.BilleteraRepository;

@Service
public class BilleteraService {
    @Autowired
    BilleteraRepository repo;

    public void gravarBilletera(Billetera billetera) {
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
        Billetera billetera = repo.findByBilleteraId(billeteraId);
        Cuenta cuenta = billetera.getCuenta(moneda);

        Transaccion transaccion = new Transaccion();
        transaccion.setFecha(new Date());
        transaccion.setEstadoId(2); // 2=aprobado 0=pendiente -1=rechazada
        transaccion.setImporte(saldo);
        transaccion.setMoneda(moneda);
        transaccion.setTipoOperacion(1); // 1=entrada, 0 =salida
        transaccion.setConceptoOperacion(conceptoOperacion);
        transaccion.setDetalle(detalle);
        transaccion.setDeUsuarioId(billetera.getPersona().getUsuario().getUsuarioId());
        transaccion.setaUsuarioId(billetera.getPersona().getUsuario().getUsuarioId());
        transaccion.setDeCuentaId(cuenta.getCuentaId());
        transaccion.setaCuentaId(cuenta.getCuentaId());

        cuenta.agregarTransacciones(transaccion);

        BigDecimal saldoActual = cuenta.getSaldo();
        BigDecimal saldoSumado = saldoActual.add(saldo);
        cuenta.setSaldo(saldoSumado);

        this.gravarBilletera(billetera);
    }

    // 2: metodo enviar saldo
    // 2.1: recibir importe(recibir billetera de origen y otra de destino)
    // 2.2: buscar cuenta por moneda
    // 2.3: actualizar saldo de la cuenta origen y destino
    // 2.4: generar 2 transacciones

    // 3: metodo consultarSaldo
    // 3.1: recibe id de billetera y la moneda de cuenta
public BigDecimal consultarSaldo(Integer billeteraId, String moneda){
    Billetera billetera = repo.findByBilleteraId(billeteraId);
    Cuenta cuenta = billetera.getCuenta(moneda);
    return cuenta.getSaldo();
}

public Billetera buscarBilleteraPorId(Integer id) {
    return repo.findByBilleteraId(id);
   
}

}