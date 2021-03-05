package mapper;
import de.bytefish.jsqlserverbulkinsert.mapping.AbstractMapping;
import models.Chargeable;

public class ChargeableMapping extends AbstractMapping<Chargeable> {

    public ChargeableMapping() {
        super("dbo", "UnitTest");

        mapInteger("Id", Chargeable::getId);
        mapInteger("PartnerID", Chargeable::getPartnerID);
        mapVarchar("Product", Chargeable::getProduct);
        mapVarchar("PartnerPurchasedPlanID", Chargeable::getPartnerPurchasedPlanID);
        mapVarchar("Plan", Chargeable::getPlan);     
        mapInteger("Usage", Chargeable::getUsage);
    }
}