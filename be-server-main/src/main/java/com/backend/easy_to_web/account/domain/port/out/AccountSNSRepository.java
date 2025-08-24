package com.backend.easy_to_web.account.domain.port.out;

import com.backend.easy_to_web.account.domain.model.AccountSNS;
import com.backend.easy_to_web.account.domain.model.SNSProvider;
import java.util.Optional;

public interface AccountSNSRepository {

  Optional<AccountSNS> findByProviderAndProviderAccountId(SNSProvider provider, String providerAccountId);

  AccountSNS save(AccountSNS accountSNSDomain);

}
