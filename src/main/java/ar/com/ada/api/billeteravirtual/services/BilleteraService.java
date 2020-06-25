package ar.com.ada.api.billeteravirtual.services;

import org.springframework.stereotype.Service;

@Service
public class BilleteraService {
    //1: metodo cargarSaldo
    //1.1: recibir un importe
    //1.2: buscar la billetera por id
    //1.3: buscar la cuenta por la moneda(no hay 2 cuentas en usd)
    //1.4:crear transaccion
    //1.5: actualizar el saldo de la billetera
    
    //2: metodo enviar saldo
    //2.1: recibir importe(recibir billetera de origen y otra  de destino)
    //2.2: buscar cuenta por moneda
    //2.3: actualizar saldo de la cuenta origen y destino
    //2.4: generar 2 transacciones

    //3: metodo consultarSaldo
    //3.1: recibe id de billetera y la moneda de cuenta

}