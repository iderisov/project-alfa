package ru.kpfu.itis.app.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.kpfu.itis.app.model.Institute;

/**
 * Created by Robert Gareev
 * 11-601 ITIS KPFU
 * 01.03.2018
 */
public interface InstitutesRepository extends JpaRepository<Institute,Long> {
}