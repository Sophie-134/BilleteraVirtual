package ar.com.ada.api.billeteravirtual.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestBody;

import ar.com.ada.api.billeteravirtual.entities.Billetera;
import ar.com.ada.api.billeteravirtual.entities.Transaccion;

@Repository
public interface BilleteraRepository extends JpaRepository<Billetera, Integer>{
  
    Billetera findByBilleteraId(Integer id);

   /* @Query("select * from transaccion where moneda = :moneda and cuenta_id = :cuenta_id")
    List<Transaccion> findByTransaccions(@Param("moneda") @Param("cuenta_id") );*/
}