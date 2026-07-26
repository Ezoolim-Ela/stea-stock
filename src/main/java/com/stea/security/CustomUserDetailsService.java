package com.stea.security;

import com.stea.entity.Utilisateur;
import com.stea.repository.UtilisateurRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UtilisateurRepository utilisateurRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Utilisateur user = utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "Utilisateur non trouvé avec l'email : " + email));

        if (user.getStatut() == Utilisateur.StatutEnum.EN_ATTENTE) {
            throw new UsernameNotFoundException(
                    "Votre compte est en attente de validation par un administrateur");
        }
        if (user.getStatut() != Utilisateur.StatutEnum.ACTIF) {
            throw new UsernameNotFoundException(
                    "Compte " + user.getStatut().name().toLowerCase() + " : acces refuse");
        }

        return new User(
                user.getEmail(),
                user.getMotDePasse(),
                List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().getLibelle().name()))
        );
    }
}
