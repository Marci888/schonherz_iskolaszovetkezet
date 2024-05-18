package hu.bme.aut.core.service;

import hu.bme.aut.core.repository.UserValidationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserValidationService {
    private final UserValidationRepository userRepository;

}
