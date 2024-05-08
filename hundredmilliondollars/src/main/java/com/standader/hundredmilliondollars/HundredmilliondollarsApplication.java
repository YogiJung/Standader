package com.standader.hundredmilliondollars;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


@SpringBootApplication
@RestController
public class HundredmilliondollarsApplication {
	
	@GetMapping("/")
	public String test_function() {
		return "Get Success";
	}

	public static void main(String[] args) {
		SpringApplication.run(HundredmilliondollarsApplication.class, args);
	}
}

// //openssl pkcs12 -export -out keystore.p12 -inkey private.key -in certificate.crt 
// 일부 경우, 개발 목적으로 임시적으로 브라우저의 보안 경고를 우회할 수 있습니다. 예를 들어, Chrome에서는 chrome://flags/#allow-insecure-localhost 주소로 이동하여 Allow invalid certificates for resources loaded from localhost. 옵션을 활성화할 수 있습니다. 하지만, 이 방법은 보안 위험을 수반하므로 일시적인 해결책으로만 사용해야 합니다.
