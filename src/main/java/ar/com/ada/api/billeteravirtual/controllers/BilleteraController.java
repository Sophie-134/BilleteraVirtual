package ar.com.ada.api.billeteravirtual.controllers;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import ar.com.ada.api.billeteravirtual.entities.Billetera;
import ar.com.ada.api.billeteravirtual.entities.Cuenta;
import ar.com.ada.api.billeteravirtual.entities.Transaccion.ResultadoTransaccionEnum;
import ar.com.ada.api.billeteravirtual.models.request.CargaRequest;
import ar.com.ada.api.billeteravirtual.models.request.EnvioSaldoRequest;
import ar.com.ada.api.billeteravirtual.models.response.SaldoResponse;
import ar.com.ada.api.billeteravirtual.models.response.TransacctionResponse;
import ar.com.ada.api.billeteravirtual.services.BilleteraService;

@RestController
public class BilleteraController {
    @Autowired
    BilleteraService billeteraService;

    /*
     * webMethod 1: consultarSaldo: GET URL: /billeteras/{id}/saldos
     * 
     * webMethods 2: cargarSaldo: POST URL:/billeteras/{id}/recargas requestBody: {
     * "moneda": "importe": }
     * 
     * 
     * webMethod 3:POST URL: /billetera/{id}/envios requestBody: { "moneda":
     * "importe": "email": "motivo": "detalleDelMotivo": }
     */
    @GetMapping("/billeteras/{id}/saldos/{moneda}")
    public ResponseEntity<?> consultarSaldo(@PathVariable Integer id, @PathVariable String moneda) {
        SaldoResponse response = new SaldoResponse();

        response.moneda = moneda;
        response.saldo = billeteraService.consultarSaldo(id, moneda);

        return ResponseEntity.ok(response);
    }

    // webMethod 1: consultarSaldo: GET URL: /billeteras/{id}/saldos
    @GetMapping("/billeteras/{id}/saldos")
    public ResponseEntity<List<SaldoResponse>> consultarSaldo(@PathVariable Integer id) {
        Billetera billetera = new Billetera();
        billetera = billeteraService.buscarBilleteraPorId(id);

        List<SaldoResponse> listSaldo = new ArrayList<>();

        for (Cuenta cuenta : billetera.getCuentas()) {

            SaldoResponse saldo = new SaldoResponse();

            saldo.saldo = cuenta.getSaldo();
            saldo.moneda = cuenta.getMoneda();

            listSaldo.add(saldo);
        }
        return ResponseEntity.ok(listSaldo);
    }

    /*
     * webMethods 2: cargarSaldo: POST URL:/billeteras/{id}/recargas requestBody: {
     * "moneda": "importe": }
     */
    @PostMapping("/billeteras/{id}/recargas")
    public ResponseEntity<TransacctionResponse> cargarSaldo(@PathVariable Integer id, @RequestBody CargaRequest recarga) {

        TransacctionResponse response = new TransacctionResponse();

        billeteraService.cargarSaldo(recarga.importe, recarga.moneda, id, "Recarga", "Desde la web");
        response.isOk = true;
        response.message = "Cargaste saldo exitosamente";

        return ResponseEntity.ok(response);
    }
    /*
     * webMethod 3:POST URL: /billetera/{id}/envios requestBody: { "moneda":
     * "importe": "email": "motivo": "detalleDelMotivo": }
     */

    @PostMapping("/billetera/{id}/envios")
    public ResponseEntity<TransacctionResponse> enviarSaldo(@PathVariable Integer id,
            @RequestBody EnvioSaldoRequest envio) {

        TransacctionResponse response = new TransacctionResponse();
        ResultadoTransaccionEnum resultado = billeteraService.enviarSaldo(envio.importe, envio.moneda, id, envio.email, envio.motivo, envio.detalle);
        
        if(resultado == ResultadoTransaccionEnum.INICIADA){

        response.isOk = true;
        response.message = "Enviaste el dinero con exito!!";

        return ResponseEntity.ok(response);
    }
    response.isOk = false;
    response.message = "Hubo un error al realizar la operacion" + resultado;
    
    return ResponseEntity.badRequest().body(response);
}

}
