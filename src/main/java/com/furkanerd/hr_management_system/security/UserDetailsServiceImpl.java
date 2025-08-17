package com.furkanerd.hr_management_system.security;

import com.furkanerd.hr_management_system.model.entity.Employee;
import com.furkanerd.hr_management_system.repository.EmployeeRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class UserDetailsServiceImpl implements UserDetailsService {

   private final EmployeeRepository employeeRepository;

    public UserDetailsServiceImpl(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Employee employee = employeeRepository.findByEmail(email)
                .orElseThrow(()-> new UsernameNotFoundException("Employee with email " + email + " not found"));
        return User.builder()
                .username(employee.getEmail())
                .password(employee.getPassword())
                .authorities("ROLE_"+employee.getRole().name())
                .accountExpired(false)
                .accountLocked(false)
                .credentialsExpired(false)
                .disabled(false) // employee.getStatus() != ACTIVE
                .build();
    }
}
