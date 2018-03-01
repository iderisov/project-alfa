package ru.kpfu.itis.app.validators;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;
import ru.kpfu.itis.app.forms.UserRegistrationForm;
import ru.kpfu.itis.app.model.RegistrationKey;
import ru.kpfu.itis.app.model.RegistrationKeyRequest;
import ru.kpfu.itis.app.model.RegistrationKeyStatus;
import ru.kpfu.itis.app.model.User;
import ru.kpfu.itis.app.repositories.RegistrationKeyRepository;
import ru.kpfu.itis.app.repositories.UsersRepository;

import java.util.Optional;

@Component
public class UserRegistrationFormValidator implements Validator {

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private RegistrationKeyRepository registrationKeyRepository;

    private UserRegistrationForm form;

    @Override
    public boolean supports(Class<?> aClass) {
        return aClass.getName().equals(UserRegistrationForm.class.getName());
    }

    @Transactional
    @Override
    public void validate(Object target, Errors errors) {
        form = (UserRegistrationForm)target;

        Optional<User> existedUser = usersRepository.findOneByLogin(form.getLogin());

        if (existedUser.isPresent()) {
            errors.reject("bad.login", "Логин занят");
        }
        if(! keyCheck(form.getKey())){
            errors.reject("bad key","Нерабочий ключ");
        }


        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "login", "empty.login", "Пустой логин");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "password", "empty.password", "Пустой пароль");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "key", "empty.key", "Пустой ключ");

    }

    private Boolean keyCheck(String keyString){
        RegistrationKey registrationKey = registrationKeyRepository.findByValue(keyString);

        if(registrationKey!= null && registrationKey.getExpiration()> System.currentTimeMillis()){

            if(registrationKey.getStatus().equals(RegistrationKeyStatus.NOT_USED)){
                registrationKey.setStatus(RegistrationKeyStatus.USED);
                addInfo(registrationKey);
                return true;
            }

        }

        return false;
    }

    private void addInfo(RegistrationKey registrationKey){
        RegistrationKeyRequest registrationKeyRequest = registrationKey.getKeyRequest();
        form.setUniversity(registrationKeyRequest.getUniversity());
        form.setInstitute(registrationKeyRequest.getInstitute());
        form.setCourse(registrationKeyRequest.getCourse());
    }


}