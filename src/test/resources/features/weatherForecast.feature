Feature: weatherForecast
  A happy holiday maker

  Scenario Outline: A happy holiday maker
    Given I like to holiday in "<city>"
    And I only like to holiday on "<dayOfWeek>"
    When I look up the the weather forecast for the next <noOfDays>
    And Check if it has rained previous days
    Then I can see the temperature is between <minTemp> to <maxTemp> degrees degrees in Bondi Beach
    And I can see that it won't be snowing for the next <noOfDays>
    Examples:
      |city           |dayOfWeek        |noOfDays |minTemp  |maxTemp  |
      |Bondi,AU       |Thursday         |14       |20       |30       |
      |Bondi,AU       |Wednesday        |16       |15       |25       |
      |Perth,AU       |Wednesday        |15       |15       |25       |
      |Adelaide,AU    |Friday           |15       |20       |30       |