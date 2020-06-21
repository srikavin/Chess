Feature: test1

  Background:
    * url baseUrl

  Scenario: test

    Given path '/api/v1/games'
    When method GET
    Then status 200
    And match response == { games: [], users: [] }