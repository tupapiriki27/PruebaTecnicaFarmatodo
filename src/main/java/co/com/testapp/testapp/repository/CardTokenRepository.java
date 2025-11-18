package co.com.testapp.testapp.repository;

import co.com.testapp.testapp.entity.CardToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CardTokenRepository extends JpaRepository<CardToken, Long> {

  Optional<CardToken> findByToken(String token);

  boolean existsByToken(String token);

}

