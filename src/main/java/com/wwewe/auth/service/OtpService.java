package com.wwewe.auth.service;

import com.wwewe.auth.exceptions.BusinessException;
import com.wwewe.auth.model.dao.EmailOtp;
import com.wwewe.auth.utils.OtpGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiFunction;

@Service
@RequiredArgsConstructor
@Slf4j
public class OtpService {

    private static final int OTP_EXPIRE_SECONDS = (int) Duration.ofMinutes(3).toSeconds();
    private static final int OTP_COOLDOWN_SECONDS = (int) Duration.ofMinutes(1).getSeconds();
    private final SessionFactory sessionFactory;
    private final OtpGenerator otpGenerator;

    public EmailOtp createByEmail(String email) {
        Objects.requireNonNull(email);

        // check we can create a new otp for this email
        Optional<EmailOtp> existing = findByEmail(email);
        if (existing.isPresent()) {
            boolean cooldown = Instant.now().isBefore(existing.get().getCreatedAt().plus(Duration.ofSeconds(OTP_COOLDOWN_SECONDS)));
            if (cooldown) {
                throw new BusinessException(String.format("Please wait for %s seconds before generating another OTP", OTP_COOLDOWN_SECONDS));
            }
        }

        EmailOtp otp = EmailOtp.builder()
                .createdAt(Instant.now())
                .verified(false)
                .value(otpGenerator.create())
                .email(email)
                .expirationInSeconds(OTP_EXPIRE_SECONDS)
                .build();
        log.info("creating otp: {}", otp);
        sessionFactory.inTransaction(session -> session.merge(otp));
        return otp;
    }

    public Optional<EmailOtp> findByEmail(String email) {
        return sessionFactory.fromSession(session -> Optional.ofNullable(
                session.createNativeQuery("select * from emailotp t where t.email = :email", EmailOtp.class)
                        .setParameter("email", email)
                        .getSingleResultOrNull()
        ));
    }

    public EmailOtp update(EmailOtp otp) {
        return sessionFactory.fromTransaction(session -> session.merge(otp));
    }

    public Iterable<EmailOtp> iterateAll() {
        int pageSize = 1000;
        int total = sessionFactory.fromSession(session -> session.createNativeQuery("select COUNT(t.email) from emailotp t", Integer.class)
                .getSingleResult());
        return getIterable(pageSize, total, this::getPage);
    }

    private List<EmailOtp> getPage(int offset, int limit) {
        return sessionFactory.fromSession(session -> session.createNativeQuery("select * from emailotp offset :offset limit :limit", EmailOtp.class)
                .setParameter("offset", offset)
                .setParameter("limit", limit)
                .list());
    }

    public void deleteAll(Set<String> emails) {
        sessionFactory.inTransaction(session -> {
            session.createNativeQuery("delete from emailotp evo where evo.email in :email", EmailOtp.class)
                    .setParameterList("email", emails)
                    .executeUpdate();
        });
    }

    private static <T> Iterable<T> getIterable(int pageSize, int total, BiFunction<Integer, Integer, List<T>> pageFunction) {
        return () -> new Iterator<>() {

            private List<T> loaded;
            private int totalExpected = total;
            private int totalReturned = 0;
            private int currentIndex = -1;

            @Override
            public boolean hasNext() {
                if (loaded == null || currentIndex + 1 >= loaded.size()) {
                    loaded = pageFunction.apply(totalReturned, pageSize);
                    if (loaded == null || loaded.isEmpty()) {
                        // early termination, given total is too large
                        totalExpected = totalReturned;
                    } else {
                        // ok, reset index
                        currentIndex = -1;
                    }
                }
                return totalReturned < totalExpected;
            }

            @Override
            public T next() {
                currentIndex += 1;
                totalReturned += 1;
                return loaded.get(currentIndex);
            }
        };
    }
}
