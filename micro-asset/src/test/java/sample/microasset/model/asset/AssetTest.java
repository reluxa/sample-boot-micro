package sample.microasset.model.asset;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.junit.Test;

import sample.EntityTestSupport;
import sample.model.account.Account;

//low: 簡易な検証が中心
public class AssetTest extends EntityTestSupport {

    @Override
    protected void setupPreset() {
        targetEntities(Account.class, CashBalance.class, Cashflow.class, CashInOut.class);
    }

    @Test
    public void 振込出金可能か判定する() {
        // 残高   +  未実現キャッシュフロー - 出金依頼拘束額 = 出金可能額 
        // 10000 + (1000 - 2000) - 8000 = 1000
        txAsset(() -> {
            fixtures.acc("test").save(repAsset);
            fixturesAsset.cb("test", LocalDate.of(2014, 11, 18), "JPY", "10000").save(repAsset);
            fixturesAsset.cf("test", "1000", LocalDate.of(2014, 11, 18), LocalDate.of(2014, 11, 20)).save(repAsset);
            fixturesAsset.cf("test", "-2000", LocalDate.of(2014, 11, 19), LocalDate.of(2014, 11, 21)).save(repAsset);
            fixturesAsset.cio("test", "8000", true).save(repAsset);

            assertThat(
                    Asset.by("test").canWithdraw(repAsset, "JPY", new BigDecimal("1000"), LocalDate.of(2014, 11, 21)),
                    is(true));
            assertThat(
                    Asset.by("test").canWithdraw(repAsset, "JPY", new BigDecimal("1001"), LocalDate.of(2014, 11, 21)),
                    is(false));
        });
    }

}
