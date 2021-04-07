package com.project.minhasfinancias.service.implentacao;

import com.project.minhasfinancias.exception.RegraNegocioException;
import com.project.minhasfinancias.model.entity.Lancamento;
import com.project.minhasfinancias.model.enums.StatusLancamento;
import com.project.minhasfinancias.model.enums.TipoLancamento;
import com.project.minhasfinancias.model.repository.LancamentoRespository;
import com.project.minhasfinancias.service.LancamentoService;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class LancamentoServiceImpl implements LancamentoService {

    private LancamentoRespository respository;

    public LancamentoServiceImpl(LancamentoRespository respository){
        this.respository = respository;
    }

    @Override
    @Transactional
    public Lancamento salvar(Lancamento lancamento) {
        validar(lancamento);
        lancamento.setStatus(StatusLancamento.PENDENTE);
        return respository.save(lancamento);
    }

    @Override
    @Transactional
    public Lancamento atualizar(Lancamento lancamento) {

        Objects.requireNonNull(lancamento.getId());
        return respository.save(lancamento);
    }

    @Override
    @Transactional
    public void deletar(Lancamento lancamento) {
        Objects.requireNonNull(lancamento);
        respository.delete(lancamento);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Lancamento> buscar(Lancamento lancamentoFiltro) {
        Example example = Example.of(lancamentoFiltro,
                ExampleMatcher.matching()
                        .withIgnoreCase()
                        .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING));

        return respository.findAll(example);
    }

    @Override
    public void atualizarStatus(Lancamento lancamento, StatusLancamento status) {
        lancamento.setStatus( status );
        atualizar(lancamento);
    }

    @Override
    public void validar(Lancamento lancamento) {

        if(lancamento.getDescricao()==null || lancamento.getDescricao().trim().equals("")){
            throw new RegraNegocioException( "Informe uma Descrição válida.");
        }
        if(lancamento.getMes()==null || lancamento.getMes()<1 || lancamento.getMes()>12){
            throw new RegraNegocioException("Informe um mês válido.");
        }
        if (lancamento.getAno()==null || lancamento.getAno().toString().length() !=4){
            throw new RegraNegocioException("Informe um Ano válido.");
        }
        if (lancamento.getUsuario()==null || lancamento.getUsuario().getId()==null){
            throw new RegraNegocioException("Informe um Usuário válido.");
        }
        if (lancamento.getValor()==null || lancamento.getValor().compareTo(BigDecimal.ZERO)<1){
            throw new RegraNegocioException("Informe um Valor válido.");
        }
        if(lancamento.getTipo()==null){
            throw new RegraNegocioException("Informe um tipo de Lançamento.");
        }
    }

    @Override
    public Optional<Lancamento> obterPorId(Long id) {
        return respository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal obterSaldoPorUsuario(Long id) {

        BigDecimal receitas = respository.obterSaldoPorTipoLancamentoEUsuario(id, TipoLancamento.RECEITA);
        BigDecimal despesas = respository.obterSaldoPorTipoLancamentoEUsuario(id, TipoLancamento.DESPESA);

        if (receitas == null){
            receitas = BigDecimal.ZERO;
        }
        if(despesas == null){
            despesas = BigDecimal.ZERO;
        }
        return receitas.subtract(despesas);
    }
}
