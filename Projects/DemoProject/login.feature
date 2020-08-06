#BDD-Feature File
@test @trial
Feature: login

# This is sample feature file covering all the basic keywords supported by Cognizant Intelligent Test Scripter BDD FEATURE FILE
Background: Common step
    Given I Open the E-commerce web site
    
#--------------
# Web automation

@Scenariooutline_1
  Scenario Outline: Valid Login scenario
  
    And I give <uname> and <pwd>
    Then I click on login button

Examples:
|uname|pwd|
|guest|guest|


@Scenariooutline_2
Scenario Outline: Invalid Login scenario

    And I give <uname> and <pwd>
    Then I click on login button

Examples:
|uname|pwd|
|guest_new|guest|
 
 
#---------------
 
# Layout verifications


@Scenario_1
Scenario: Positive visual testing

Then Assert if the username textbox is aligned above the password text box
And Assert if the login button is below the password button


@Scenario_2
Scenario: Negative visual testing

Then Assert if the password textbox is aligned above the username text box
And Assert if the login button is above the password button

#---------------

# Assert element image
@Docstring_1
Scenario: Doc string without Parameter
Then I check if the company logo is displayed as expected
"""
Here the logo in the laoded web page is asserted with the image file whose path is provided in the input colum
-------
Happy Testing!!!
"""

@Docstring_2
Scenario Outline: Doc string with Parameter
Then I assert if the company logo in the page is same as <image_path>
"""
Here the logo in the laoded web page is asserted with the image file whose path is provided in the input colum
-------
Happy Testing!!!
"""
Examples:
|image_path|
|sample path|

#----------------




  