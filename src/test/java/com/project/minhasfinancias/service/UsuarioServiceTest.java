package com.project.minhasfinancias.service;

import com.project.minhasfinancias.exception.RegraNegocioException;
import com.project.minhasfinancias.model.entity.Usuario;
import com.project.minhasfinancias.model.repository.UsuarioRepository;
import com.project.minhasfinancias.service.implentacao.UsuarioServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class UsuarioServiceTest {

    @SpyBean
    UsuarioServiceImpl service;

    @MockBean
    UsuarioRepository repository;

    @Test
    public void deveSalvarUmUsuario(){
        //cenário
        Mockito.doNothing().when(service).validarEmail(Mockito.anyString());
        Usuario usuario = Usuario.builder()
                .nome("nome")
                .email("email@email.com")
                .senha("senha")
                .build();
        Mockito.when(repository.save(Mockito.any(Usuario.class))).thenReturn(usuario);

        //Ação
        Usuario usuarioSalvo = service.SalvaUsuario(new Usuario());

        //Verificação
        Assertions.assertNotNull(usuarioSalvo);
        Assertions.assertEquals(1l,usuarioSalvo.getId());
        Assertions.assertEquals("nome",usuarioSalvo.getNome());
        Assertions.assertEquals("email@email.com",usuarioSalvo.getEmail());
        Assertions.assertEquals("senha",usuarioSalvo.getSenha());
    }

    @Test
    public void naoDeveSalvarUmUsuarioComEmailJaCadastrado(){
        //cenario
        String email="email@email.com";
        Usuario usuario = Usuario.builder().email(email).build();
        Mockito.doThrow(RegraNegocioException.class).when(service).validarEmail(email);

        //ação
        service.SalvaUsuario(usuario);

        //verificação
        Mockito.verify(repository, Mockito.never()).save(usuario);
    }


    @Test
    public void deveValidarEmail(){
        //cenario
        Mockito.when(repository.existsByEmail(Mockito.anyString())).thenReturn(false);

        //ação
        service.validarEmail("email@email.com");
    }

    @Test
    public void deveLancarErroAoValidarEmailQuandoExistirEmailCadastradp(){

        Mockito.when(repository.existsByEmail(Mockito.anyString())).thenReturn(true);

        service.validarEmail("usuario@email.com");
    }
    @Test
    public void deveAutenticarUmUsuarioComSucesso(){
        //cenário
        String email="email@email.com";
        String senha="senha";

        Usuario usuario=Usuario.builder().senha(senha).email(email).id(1l).build();
        Mockito.when(repository.findByEmail(email)).thenReturn(Optional.of(usuario));
        //ação
        Usuario result = service.autenticar(email, senha);

        //vericação
        Assertions.assertNotNull(result);
    }

    @Test
    public void deveLancarErroQuandoNãoEncontrarUsuarioCadastradoComOEmailInformado(){
        //cenario
        Mockito.when(repository.findByEmail(Mockito.anyString())).thenReturn(Optional.empty());

        //ação
        service.autenticar("email@email.com","senha");
    }

    @Test
    public void deveLancarErroQuandoSenhaNaoBater(){
        //cenario
        String senha = "senha";
        Usuario usuario = Usuario.builder().email("email@email.com").senha(senha).build();
        Mockito.when(repository.findByEmail(Mockito.anyString())).thenReturn(Optional.of(usuario));

        //ação
        service.autenticar("email@email.com", "234");
    }



}
