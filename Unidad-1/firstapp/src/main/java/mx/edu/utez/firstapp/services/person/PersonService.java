package mx.edu.utez.firstapp.services.person;

import mx.edu.utez.firstapp.config.ApiResponse;
import mx.edu.utez.firstapp.models.person.Person;
import mx.edu.utez.firstapp.models.person.PersonRepository;
import mx.edu.utez.firstapp.models.role.Role;
import mx.edu.utez.firstapp.models.user.User;
import mx.edu.utez.firstapp.models.user.UserRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
import java.util.Optional;
import java.util.Set;

@Service
@Transactional
public class PersonService {
    private final PersonRepository repository;
    private final UserRepository userRepository;

    private final PasswordEncoder encoder;


    public PersonService(PersonRepository repository, UserRepository userRepository, PasswordEncoder encoder) {
        this.repository = repository;
        this.userRepository = userRepository;
        this.encoder = encoder;
    }

    @Transactional(readOnly = true)
    public ResponseEntity<ApiResponse> getAll() {
        return new ResponseEntity<>(new ApiResponse(repository.findAll(), HttpStatus.OK), HttpStatus.OK);
    }

    @Transactional(readOnly = true)
    public ResponseEntity<ApiResponse> getAllPagination(String searchParam, Pageable pageable) {
        return new ResponseEntity<>(new ApiResponse(repository.findByNameContainingIgnoreCaseOrSurnameContainingIgnoreCaseOrCurpContainingIgnoreCase(searchParam, searchParam, searchParam, pageable), HttpStatus.OK), HttpStatus.OK);
    }

    //Flush = Guarda directamente en la base de datos
    @Transactional(rollbackFor = {SQLException.class})
    public ResponseEntity<ApiResponse> save(Person person) {
        Optional<Person> foundPerson = repository.findByCurp(person.getCurp());
        if (foundPerson.isPresent()) {
            return new ResponseEntity<>(new ApiResponse(HttpStatus.BAD_REQUEST, true, "RecordAlreadyExist"), HttpStatus.BAD_REQUEST);
        }
        if (person.getUser() != null) {
            person.getUser().setPassword(
                    encoder.encode(person.getUser().getPassword())
            );
            person.getUser().setPerson(person);
            Optional<User> foundUser = userRepository.findFirstByUsername(person.getUser().getUsername());
            if (foundUser.isPresent()) {
                return new ResponseEntity<>(new ApiResponse(HttpStatus.BAD_REQUEST,
                        true, "RecordAlreadyExist"), HttpStatus.BAD_REQUEST);
            }
            Set<Role> roles = person.getUser().getRoles();
            person.getUser().setRoles(null);
            person = repository.saveAndFlush(person);
            for (Role role : roles) {
                if (userRepository.saveUserRole(person.getUser().getId(), role.getId()) <= 0) {
                    return new ResponseEntity<>(new ApiResponse(HttpStatus.BAD_REQUEST, true, "RoleNotAttached"), HttpStatus.BAD_REQUEST);
                }
            }
        } else {
            person = repository.saveAndFlush(person);
        }
        return new ResponseEntity<>(new ApiResponse(person, HttpStatus.OK), HttpStatus.OK);
    }

    @Transactional(rollbackFor = {SQLException.class})
    public ResponseEntity<ApiResponse> update(Person person) {
        Optional<Person> foundPerson = repository.findById(person.getId());
        if (foundPerson.isEmpty())
            return new ResponseEntity<>(
                    new ApiResponse(HttpStatus.NOT_FOUND, true, "NotDataFound"),
                    HttpStatus.BAD_REQUEST
            );
        Optional<Person> existingCurp = repository.findByCurpAndIdNot(person.getCurp(), person.getId());
        return null;
    }
}
