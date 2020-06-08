package com.micanasta.controller;

import com.micanasta.dto.CompraUpdateDto;
import com.micanasta.dto.CrearFamiliaDTO;
import com.micanasta.dto.CompraCreateDto;
import com.micanasta.exception.ExistingFamilyFoundException;
import com.micanasta.service.CompraService;
import com.micanasta.service.ProductoService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Date;

@RestController
@CrossOrigin(origins = "*", methods= {RequestMethod.GET,RequestMethod.POST,RequestMethod.PUT,RequestMethod.DELETE})
@RequiredArgsConstructor
public class CompraController {

    @Autowired
    private CompraService compraService;

    @PostMapping("/compras")
    public ResponseEntity<?> create(@Valid @RequestBody CompraCreateDto compraDto) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(compraService.create(compraDto));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @PutMapping("/compras")
    public ResponseEntity<?> update(@Valid @RequestBody CompraUpdateDto historialDto){
        try {
            return ResponseEntity.status(HttpStatus.OK).body(compraService.update(historialDto));
        }
        catch(Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @GetMapping("/compras")
    public ResponseEntity<?> getCompras(@RequestParam long idFamilia, @RequestParam String dni,
                                            @RequestParam @DateTimeFormat(pattern="yyyy-MM-dd") Date fechaInicio,
                                            @RequestParam @DateTimeFormat(pattern="yyyy-MM-dd") Date fechaFin) throws Exception {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(compraService.getCompras(idFamilia, dni, fechaInicio
                    , fechaFin));
        }
        catch(Exception e){
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }

    }
}
