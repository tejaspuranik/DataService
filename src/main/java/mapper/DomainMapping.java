package mapper;
import de.bytefish.jsqlserverbulkinsert.mapping.AbstractMapping;
import models.Domain;

public class DomainMapping extends AbstractMapping<Domain> {

    public DomainMapping() {
        super("dbo", "UnitTest");

        mapInteger("Id", Domain::getId);
        mapVarchar("PartnerPurchasedPlanID", Domain::getPartnerPurchasedPlanID);
        mapVarchar("Plan", Domain::getDomain);     
    }
}