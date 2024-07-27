package com.wwewe.auth.scheduler;

import com.wwewe.auth.service.OtpService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j
public class CleanOtpScheduler {

    private final OtpService otpService;

    // run every day midnight
    @Scheduled(cron = "0 0 0 * * *")
//    @EventListener
    public void cleanUpOtp() {

//        log.info("creating OTPs");
//        IntStream.range(0, 10).forEach(i -> otpService.create("test@wll.dev"));
//        Thread.sleep(5 * 1000);
//        Page<EmailValidationOtp> otpPage = otpService.findAll(Pageable.ofSize(PAGE_SIZE));

        log.info("cleaning otps");

        Set<String> toDelete = new HashSet<>();

        otpService.iterateAll()
                .forEach(otp -> {
                    log.debug("checking otp: {}", otp);
                    if (otp.isExpired()) {
                        toDelete.add(otp.getEmail());
                    }
                });

        log.debug("amount of otps to delete: {}", toDelete.size());

        otpService.deleteAll(toDelete);
    }
}
