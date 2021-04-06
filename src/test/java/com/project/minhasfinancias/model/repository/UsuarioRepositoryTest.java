package com.project.minhasfinancias.model.repository;

import com.project.minhasfinancias.model.entity.Usuario;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class UsuarioRepositoryTest {

    @Autowired
    UsuarioRepository repository;

    @Autowired
    TestEntityManager entityManager;

    @Test
    public  void deveVerificarAExistenciaDeUmEmail(){
        //cenário
        Usuario usuario = criarUsuario();
        entityManager.persist(usuario);

        //ação/ execução

        boolean result = repository.existsByEmail(usuario.getEmail());
        //verificação
        Assertions.assertTrue(result);
    }

    @Test
    public void deveRetornarFalsoQuandoNaoHouverUsuarioCadastroComOEmail(){

        boolean result = repository.existsByEmail("usuario@email.com");

        Assertions.assertFalse(result);
    }

    @Test
    public void devePersistirUmUsuarioNaBaseDeDados(){

        Usuario usuario = criarUsuario();
        Usuario usuarioSalvo = repository.save(usuario);
        System.out.println(usuarioSalvo);

        Assertions.assertNotNull(usuarioSalvo.getId());
    }

    @Test
    public void deveBuscarUmUsuarioPorEmail(){
        //cenario
        Usuario usuario = criarUsuario();
        entityManager.persist(usuario);

        //verificacao
        Optional<Usuario> result = repository.findByEmail("usuario@email.com");

        Assertions.assertTrue(result.isPresent());
    }
    @Test
    public void deveRetornarVazioAoBuscarUsuarioPorEmailQuandoNaoExistiNaBase(){
        Optional<Usuario> result = repository.findByEmail("usuario@email.com");
        Assertions.assertFalse(result.isPresent());

    }
    public static Usuario criarUsuario(){

        return Usuario
                .builder()
                .nome("usuario")
                .email("usuario@email.com")
                .senha("senha")
                .build();
    }

}
