package com.project.minhasfinancias.api.resource;

import com.project.minhasfinancias.api.dto.AtualizaStatusDTO;
import com.project.minhasfinancias.api.dto.LancamentoDTO;
import com.project.minhasfinancias.exception.RegraNegocioException;
import com.project.minhasfinancias.model.entity.Lancamento;
import com.project.minhasfinancias.model.entity.Usuario;
import com.project.minhasfinancias.model.enums.StatusLancamento;
import com.project.minhasfinancias.model.enums.TipoLancamento;
import com.project.minhasfinancias.service.LancamentoService;
import com.project.minhasfinancias.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/lancamentos")
public class LancamentoResource {

    private final LancamentoService service;
    private final UsuarioService usuarioService;


    @PostMapping
    public ResponseEntity salvar (@RequestBody LancamentoDTO dto){
        try{
            Lancamento entidade = converter(dto);
            entidade = service.salvar(entidade);
            return new ResponseEntity(entidade, HttpStatus.CREATED);
        }catch ( RegraNegocioException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @DeleteMapping("{id}")
    public ResponseEntity deletar(@PathVariable("id") Long id){
        return service.obterPorId(id).map(entidade -> {
            service.deletar(entidade);
            return new ResponseEntity(HttpStatus.NO_CONTENT);
        }).orElseGet(()->
                new ResponseEntity("Lançamento não encontrado na base de Dados.", HttpStatus.BAD_REQUEST));
    }

    @GetMapping
    public ResponseEntity buscar(
            @RequestParam(value = "descricao", required = false) String descricao,
            @RequestParam(value = "mes", required = false) Integer mes,
            @RequestParam(value = "ano", required = false) Integer ano,
            @RequestParam("usuario") Long idUsuario
            ){
        Lancamento lancamentoFiltro = new Lancamento();
        lancamentoFiltro.setDescricao(descricao);
        lancamentoFiltro.setMes(mes);
        lancamentoFiltro.setAno(ano);

        Optional<Usuario> usuario = usuarioService.obterPorId(idUsuario);
        if (!usuario.isPresent()){
            return ResponseEntity.badRequest().body("Não foi possivel realizar a consulta. Usuário não encontrado na Base de Dados");
        }else {
            lancamentoFiltro.setUsuario(usuario.get());
        }
        List<Lancamento> lancamentos=service.buscar(lancamentoFiltro);
        return ResponseEntity.ok(lancamentos);
    }



    @PutMapping("{id}")
    public ResponseEntity atualizar(@PathVariable("id") Long id, @RequestBody LancamentoDTO dto) {
        return service.obterPorId(id).map(entity -> {
            try {
                Lancamento lancamento = converter(dto);
                lancamento.setId(entity.getId());
                service.atualizar(lancamento);
                return ResponseEntity.ok(lancamento);
            } catch (RegraNegocioException e) {
                return ResponseEntity.badRequest().body(e.getMessage());
            }
        }).orElseGet(() ->
                new ResponseEntity("Lancamento não encontrado na base de dado", HttpStatus.BAD_REQUEST));
    }

    @PutMapping("{id}/atualizar-status")
    public ResponseEntity atualizarStatus (@PathVariable ("id") Long id, @RequestBody AtualizaStatusDTO dto){
        return service.obterPorId(id).map(entity -> {
            StatusLancamento statusSelecionado = StatusLancamento.valueOf(dto.getStatus());

            if(statusSelecionado == null){
                return ResponseEntity.badRequest().body("Não foi possivel atualizar o status do lançamento, envie um status válido.");
            }
            try {
                entity.setStatus(statusSelecionado);
                service.atualizar(entity);
                return ResponseEntity.ok(entity);
            }catch (RegraNegocioException e){
                return ResponseEntity.badRequest().body(e.getMessage());
            }
        }).orElseGet(()->
                new ResponseEntity("Lancamento não encontrado na base de Dados", HttpStatus.BAD_REQUEST));
    }

    private Lancamento converter(LancamentoDTO dto){
        Lancamento lancamento = new Lancamento();
        lancamento.setId(dto.getId());
        lancamento.setDescricao(dto.getDescricao());
        lancamento.setAno(dto.getAno());
        lancamento.setMes(dto.getMes());
        lancamento.setValor(dto.getValor());

        Usuario usuario = usuarioService.obterPorId(dto.getUsuario())
                .orElseThrow(()->new RegraNegocioException("Usuario não encontrado para o id informado."));

        lancamento.setUsuario(usuario);

        if (dto.getTipo()!= null){
            lancamento.setTipo(TipoLancamento.valueOf(dto.getTipo()));
        }

        if(dto.getStatus() != null){
            lancamento.setStatus(StatusLancamento.valueOf(dto.getStatus()));
        }

        return lancamento;
    }

}
