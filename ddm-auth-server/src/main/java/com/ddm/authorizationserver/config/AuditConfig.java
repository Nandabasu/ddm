package com.ddm.authorizationserver.config;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.ddm.authorizationserver.model.AuthUserDetail;
import com.ddm.authorizationserver.model.User;
import com.ddm.authorizationserver.repository.UserDetailRepository;

@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorProvider" )
public class AuditConfig {

	
 	@Bean
    public AuditorAware<Long> auditorProvider() {
        return new SpringSecurityAuditAwareImpl();
    }
}
class SpringSecurityAuditAwareImpl implements AuditorAware<Long> {

	@Autowired
	UserDetailRepository userRepo;
	
	@Override
    public Optional<Long> getCurrentAuditor() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null ||
                !authentication.isAuthenticated() ||
                authentication instanceof AnonymousAuthenticationToken) {
            return Optional.empty();
        }

        AuthUserDetail userPrincipal = (AuthUserDetail) authentication.getPrincipal();
        User user = userRepo.findByUsername(userPrincipal.getUsername()).get();
        return Optional.ofNullable(user.getId());
    }
    
}