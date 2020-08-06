#BDD-Feature File

Feature: LogIn	
Login Scenarios
When I navigate to the URL <URL>
And I give the username <username> and the password <password>
And I click on the Log in Button
Then I will be navigated to the Catalog URL <CatalogURL> 

Example:

|URL|username|password|CatalogURL|
|http://localhost:9090/DemoApplication/Web_App/Home.htm|guest|guest|http://localhost:9090/DemoApplication/Web_App/Catalog.htm|

