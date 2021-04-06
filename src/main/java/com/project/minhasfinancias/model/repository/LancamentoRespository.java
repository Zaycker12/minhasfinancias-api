package com.project.minhasfinancias.model.repository;

import com.project.minhasfinancias.model.entity.Lancamento;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LancamentoRespository extends JpaRepository<Lancamento, Long> {

}
