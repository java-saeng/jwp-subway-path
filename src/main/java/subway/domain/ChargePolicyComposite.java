package subway.domain;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.util.List;

@Primary
@Component
public class ChargePolicyComposite implements SubwayFarePolicy, SubwayDiscountPolicy {

    private final List<SubwayFarePolicy> farePolicies;
    private final List<SubwayDiscountPolicy> discountPolicies;

    public ChargePolicyComposite(
            final List<SubwayFarePolicy> farePolicies,
            final List<SubwayDiscountPolicy> discountPolicies
    ) {
        this.farePolicies = farePolicies;
        this.discountPolicies = discountPolicies;
    }

    @Override
    public Money calculate(final Route route) {
        return farePolicies.stream()
                           .map(it -> it.calculate(route))
                           .reduce(Money.ZERO, Money::add);
    }

    @Override
    public Money discount(final DiscountCondition discountCondition, final Money price) {
        return discountPolicies.stream()
                               .reduce(price, (money, subwayDiscountPolicy) ->
                                               subwayDiscountPolicy.discount(discountCondition, money),
                                       (money1, money2) -> money2);
    }
}
