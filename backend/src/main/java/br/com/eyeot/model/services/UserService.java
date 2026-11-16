package br.com.eyeot.model.services;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import br.com.eyeot.enuns.RolesEnum;
import br.com.eyeot.model.dtos.LoginRequestDTO;
import br.com.eyeot.model.dtos.ResponseDTO;
import br.com.eyeot.model.dtos.UserDTO;
import br.com.eyeot.model.dtos.UserPasswordUpdateDTO;
import br.com.eyeot.model.dtos.UserResumeDTO;
import br.com.eyeot.model.dtos.UserUpdateDTO;
import br.com.eyeot.model.dtos.forms.UserFormDTO;
import br.com.eyeot.model.entities.User;
import br.com.eyeot.model.repositories.DeviceRepository;
import br.com.eyeot.model.repositories.DomainRepository;
import br.com.eyeot.model.repositories.UserRepository;
import br.com.eyeot.security.TokenService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {
    
    private final UserRepository userRepository;
    private final DeviceRepository deviceRepository;
    private final DomainRepository domainRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;

    public ResponseEntity<ResponseDTO<Void>> login(LoginRequestDTO loginRequestDTO){

        Optional<User> user = this.userRepository.findByEmail1(loginRequestDTO.email());

        if (user.isEmpty()) {
            return ResponseEntity.badRequest().body(ResponseDTO.<Void>builder().status(HttpStatus.BAD_REQUEST.toString()).descricao("Email ou Senha incorretos!").build());
        }
        if(passwordEncoder.matches(loginRequestDTO.password(), user.get().getPassword())) {
            ResponseCookie token = this.tokenService.generarToken(user.get());

            return ResponseEntity.ok()
                    .header(HttpHeaders.SET_COOKIE, token.toString())        
            .body(ResponseDTO.<Void>builder()
                    .status(HttpStatus.OK.toString())
                    .descricao("Usuário logado com sucesso!")
                    .build()); 
        }
        return ResponseEntity.internalServerError().body(ResponseDTO.<Void>builder().status(HttpStatus.BAD_REQUEST.toString()).descricao("Email ou Senha incorretos!").build());
    }

    @Transactional
    public ResponseEntity<ResponseDTO<Void>> registrarUsuario(UserFormDTO userFormDTO){

        Optional<User> user = this.userRepository.findByEmail1(userFormDTO.email1());

        if (user.isPresent()) {
            return ResponseEntity.badRequest().body(ResponseDTO.<Void>builder().status(HttpStatus.BAD_REQUEST.toString()).descricao("Usuário já cadastrado!").data(null).build());
        }

        User novoUsuario = new User(null,
                    RolesEnum.ROLE_USER, 
                    userFormDTO.name(),
                    userFormDTO.phone(), 
                    userFormDTO.email1(),
                    userFormDTO.email2(),  
                    passwordEncoder.encode(userFormDTO.password()),
                    null,
                    null);
        
        userRepository.save(novoUsuario);


        return ResponseEntity.ok().body(ResponseDTO.<Void>builder()
                .status(HttpStatus.OK.toString())
                .descricao("Usuário registrado com sucesso!").build()); 
    }

    public ResponseEntity<ResponseDTO<List<UserResumeDTO>>> listarUsuariosResumidos(){
        try{
            List<User> entities = userRepository.findAll();
            List<UserResumeDTO> dtos = entities.stream().map((user) -> 
                new UserResumeDTO(user.getId_user(), user.getName())
            ).toList();

            return ResponseEntity.ok().body(ResponseDTO.<List<UserResumeDTO>>builder()
                .status(HttpStatus.OK.toString())
                .descricao("Listagem de Usuarios com seus dados resumidos.")
                .data(dtos).build());
        } catch (Exception e){
            return ResponseEntity.internalServerError().body(ResponseDTO.<List<UserResumeDTO>>builder()
                .status(HttpStatus.INTERNAL_SERVER_ERROR.toString())
                .descricao("Erro ao listar usuários.")
                .data(null).build());
        }
    }

    public ResponseEntity<ResponseDTO<List<UserDTO>>> listarTodosUsuariosPaginado(String busca, UserDTO userDTO, Integer page, Integer size){
        if(!userDTO.role().equals(RolesEnum.ROLE_ADMIN)){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ResponseDTO.<List<UserDTO>>builder()
                .status(HttpStatus.FORBIDDEN.toString())
                .descricao("Acesso negado. Usuário não possui permissão para listar todos os usuários.")
                .data(null).build());
        }

        try{
            Sort sort = Sort.by(Sort.Order.asc("name"));

            Pageable pageableOrdenado = PageRequest.of(
                page,
                size,
                sort
            );

            Page<User> entities;

            if (busca != null && !busca.isBlank()) {
                entities = userRepository.buscarUsuariosPaginado(busca, pageableOrdenado);
            }
            else {
                entities = userRepository.findAll(pageableOrdenado);
            }   

            List<UserDTO> dtos = entities.getContent().stream().map((user) -> 
                new UserDTO(user.getId_user(),
                            user.getPapel(), 
                            user.getName(), 
                            user.getPhone(), 
                            user.getEmail1(),
                            user.getEmail2())).toList();

            return ResponseEntity.ok().body(ResponseDTO.<List<UserDTO>>builder()
                .status(HttpStatus.OK.toString())
                .descricao("Listagem de Usuarios com seus dados resumidos.")
                .data(dtos)
                .pageNumber(entities.getNumber())
                .totalElements(entities.getTotalElements())
                .totalPages(entities.getTotalPages())
                .pageSize(entities.getSize()).build());

        } catch (Exception e){
            return ResponseEntity.internalServerError().body(ResponseDTO.<List<UserDTO>>builder()
                .status(HttpStatus.INTERNAL_SERVER_ERROR.toString())
                .descricao("Erro ao listar usuários.")
                .data(null).build());
        }
    }

    @Transactional
    public ResponseEntity<ResponseDTO<Void>> alterarRole(Integer id, RolesEnum novaRole, UserDTO userDTO){ 

        if(!userDTO.role().equals(RolesEnum.ROLE_ADMIN)){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ResponseDTO.<Void>builder()
                .status(HttpStatus.FORBIDDEN.toString())
                .descricao("Acesso negado. Usuário não possui permissão para alterar o papel de outros usuários.")
                .data(null).build());
        }

        try{
            User usuario =
            userRepository.findById(id)
                .orElseThrow(() ->
                    new EntityNotFoundException(
                        "Usuário não encontrado"
                    )
                );

            usuario.setPapel(novaRole);

            return ResponseEntity.ok(ResponseDTO.<Void>builder().descricao("Papel de Usuário atualizada!").status(HttpStatus.OK.toString()).build());
 
        } catch(Exception e){
            return ResponseEntity.badRequest().body(ResponseDTO.<Void>builder().descricao("Erro ao atualizar papel!").status(HttpStatus.OK.toString()).build());
        }
        
    }

    @Transactional
    public ResponseEntity<ResponseDTO<Void>> excluirUsuario(Integer idUser, UserDTO userDTO) {

        if(!userDTO.role().equals(RolesEnum.ROLE_ADMIN)){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ResponseDTO.<Void>builder()
                .status(HttpStatus.FORBIDDEN.toString())
                .descricao("Acesso negado. Usuário não possui permissão para excluir outros usuários.")
                .data(null).build());
        }

        try{
            User usuario = userRepository.findById(idUser)
                .orElseThrow(() ->
                    new EntityNotFoundException("Usuário não encontrado.")
                );

            deviceRepository.desvincularOwner(idUser);

            deviceRepository.desvincularDominios(idUser);

            domainRepository.excluirPorUsuario(idUser);

            userRepository.delete(usuario);
            
            return ResponseEntity.ok().body(ResponseDTO.<Void>builder()
                .status(HttpStatus.OK.toString())
                .descricao("Usuário excluído com sucesso.")
                .data(null).build());

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ResponseDTO.<Void>builder()
                .status(HttpStatus.INTERNAL_SERVER_ERROR.toString())
                .descricao("Erro ao excluir usuário.")
                .data(null).build());
        }
    }

    @Transactional
    public ResponseEntity<ResponseDTO<UserDTO>> atualizarPerfil(Integer idUser, UserUpdateDTO dados) {
        try{
            User usuario = userRepository.findById(idUser)
                .orElseThrow(() ->
                    new EntityNotFoundException("Usuário não encontrado.")
                );

            String nome = dados.nameUser().trim();

            String telefone = dados.phone() == null || dados.phone().isBlank()
                ? null
                : dados.phone().trim();

            String email2 = dados.email2() == null || dados.email2().isBlank()
                ? null
                : dados.email2().trim().toLowerCase(Locale.ROOT);

            if (email2 != null &&
                email2.equalsIgnoreCase(usuario.getEmail1())) {

                throw new IllegalArgumentException(
                    "O e-mail secundário deve ser diferente do e-mail principal."
                );
            }

            if (email2 != null &&
                userRepository.emailEmUsoPorOutroUsuario(email2, idUser)) {

                throw new IllegalArgumentException(
                    "O e-mail secundário já está cadastrado em outra conta."
                );
            }

            usuario.setName(nome);
            usuario.setPhone(telefone);
            usuario.setEmail2(email2);

            User usuarioAtualizado = userRepository.save(usuario);

            UserDTO usuarioAtualizadoDTO = new UserDTO(
                usuarioAtualizado.getId_user(),
                usuarioAtualizado.getPapel(),
                usuarioAtualizado.getName(),
                usuarioAtualizado.getPhone(),
                usuarioAtualizado.getEmail1(),
                usuarioAtualizado.getEmail2()
            );

            return ResponseEntity.ok(
                ResponseDTO.<UserDTO>builder()
                    .descricao("Dados do usuário atualizados com sucesso.")
                    .status(HttpStatus.OK.toString())
                    .data(usuarioAtualizadoDTO)
                    .build()
            );
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ResponseDTO.<UserDTO>builder()
                    .descricao("Usuário não encontrado.")
                    .status(HttpStatus.NOT_FOUND.toString())
                    .data(null)
                    .build()
                );
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                .body(ResponseDTO.<UserDTO>builder()
                    .descricao("Erro ao atualizar perfil do usuário. Confira os dados informados.")
                    .status(HttpStatus.BAD_REQUEST.toString())
                    .data(null)
                    .build()
                );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ResponseDTO.<UserDTO>builder()
                    .descricao("Erro ao atualizar perfil do usuário.")
                    .status(HttpStatus.INTERNAL_SERVER_ERROR.toString())
                    .data(null)
                    .build()
                );
        }
        

    }


    @Transactional
    public ResponseEntity<ResponseDTO<Void>> alterarSenha(Integer idUser, UserPasswordUpdateDTO dados) {

        try{
            User usuario = userRepository.findById(idUser)
            .orElseThrow(() ->
                new EntityNotFoundException("Usuário não encontrado.")
            );

            if (!passwordEncoder.matches(
                dados.currentPassword(),
                usuario.getPassword()
            )) {
                throw new IllegalArgumentException("A senha atual está incorreta.");
            }

            if (passwordEncoder.matches(
                dados.newPassword(),
                usuario.getPassword()
            )) {
                throw new IllegalArgumentException(
                    "A nova senha deve ser diferente da senha atual."
                );
            }

            usuario.setPassword(
                passwordEncoder.encode(dados.newPassword())
            );

            userRepository.save(usuario);

            return ResponseEntity.ok().body(ResponseDTO.<Void>builder()
                .status(HttpStatus.OK.toString())
                .descricao("Senha atualizada com sucesso.")
                .data(null)
                .build()
            );

        }catch(IllegalArgumentException e){
            return ResponseEntity.badRequest().body(ResponseDTO.<Void>builder()
                .status(HttpStatus.BAD_REQUEST.toString())
                .descricao(e.getMessage())
                .data(null)
                .build()
            );
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResponseDTO.<Void>builder()
                .status(HttpStatus.NOT_FOUND.toString())
                .descricao("Usuário não encontrado.")
                .data(null)
                .build()
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ResponseDTO.<Void>builder()
                .status(HttpStatus.INTERNAL_SERVER_ERROR.toString())
                .descricao("Erro ao atualizar senha do usuário.")
                .data(null)
                .build()
            );

        }

    }

    @Transactional
    public ResponseDTO<String> redefinirSenha(Integer idUser, UserDTO userDTO) {

        if(!userDTO.role().equals(RolesEnum.ROLE_ADMIN)){
            return ResponseDTO.<String>builder()
                .status(HttpStatus.FORBIDDEN.toString())
                .descricao("Acesso negado. Usuário não possui permissão para resetar a senha de outros usuários.")
                .data(null).build();
        }

        User usuario = userRepository.findById(idUser)
            .orElseThrow(() ->
                new EntityNotFoundException("Usuário não encontrado.")
            );

        String novaSenha = gerarSenhaTemporaria();

        usuario.setPassword(passwordEncoder.encode(novaSenha));

        userRepository.save(usuario);

        return ResponseDTO.<String>builder()
            .status(HttpStatus.OK.toString())
            .descricao("Senha resetada com sucesso.")
            .data(novaSenha)
            .build();
    }


    private String gerarSenhaTemporaria() {

        final String maiusculas = "ABCDEFGHJKLMNPQRSTUVWXYZ";
        final String minusculas = "abcdefghijkmnopqrstuvwxyz";
        final String numeros = "23456789";
        final String especiais = "@#$%&*!";

        final String todosCaracteres = maiusculas + minusculas + numeros + especiais;

        SecureRandom random = new SecureRandom();

        List<Character> caracteres = new ArrayList<>();

        caracteres.add(
            maiusculas.charAt(random.nextInt(maiusculas.length()))
        );

        caracteres.add(
            minusculas.charAt(random.nextInt(minusculas.length()))
        );

        caracteres.add(
            numeros.charAt(random.nextInt(numeros.length()))
        );

        caracteres.add(
            especiais.charAt(random.nextInt(especiais.length()))
        );

        for (int i = 0; i < 8; i++) {
            caracteres.add(
                todosCaracteres.charAt(random.nextInt(todosCaracteres.length()))
            );
        }

        Collections.shuffle(caracteres, random);

        StringBuilder senha = new StringBuilder();

        for (Character caractere : caracteres) {
            senha.append(caractere);
        }

        return senha.toString();
    }

}
