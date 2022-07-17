package com.rdstation.domain;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.OptionalInt;
import java.util.stream.Collectors;

public class CustomerSuccessBalancing {

  private final List<CustomerSuccess> customerSuccess;
  private final List<Customer> customers;
  private final List<Integer> customerSuccessAway;

  public CustomerSuccessBalancing(List<CustomerSuccess> customerSuccess,
      List<Customer> customers,
      List<Integer> customerSuccessAway) {
    this.customerSuccess = customerSuccess;
    this.customers = customers;
    this.customerSuccessAway = customerSuccessAway;
  }


  public int run() {
    int id = 0;
    long greaterNumberOfCustomersServed = 0;

    List<CustomerSuccess> customerSuccessSorted = sortAndRemoveAlwaysFromCustomerSuccessList();

    for (CustomerSuccess cs : customerSuccessSorted) {
      HashSet<Customer> customersServed = findCustomersServedByCustomerSuccess(cs.getScore());

      customers.removeAll(customersServed);

      if (customersServed.size() > greaterNumberOfCustomersServed) {
        id = customersServed.size() == greaterNumberOfCustomersServed ? 0 : cs.getId();
        greaterNumberOfCustomersServed = customersServed.size();
      } else {
        id = customersServed.size() == greaterNumberOfCustomersServed ? 0 : id;
      }
    }

    return id;
  }

  private List<CustomerSuccess> sortAndRemoveAlwaysFromCustomerSuccessList() {
    OptionalInt minCustomerScoreOptional =
        customers.stream().mapToInt(Customer::getScore).min();
    int minCustomerScore = minCustomerScoreOptional.orElse(0);
    HashSet<Integer> customerSuccessAwayHashSet = new HashSet<>(customerSuccessAway);

    return customerSuccess.stream()
        .filter(cs -> !customerSuccessAwayHashSet.contains(cs.getId()))
        .filter(cs -> cs.getScore() >= minCustomerScore)
        .sorted(Comparator.comparingInt(CustomerSuccess::getScore))
        .collect(Collectors.toList());
  }

  private HashSet<Customer> findCustomersServedByCustomerSuccess(int customerSuccessScore) {
    return customers.stream().filter(customer -> customer.getScore() <= customerSuccessScore)
        .collect(Collectors.toCollection(HashSet::new));
  }
}
