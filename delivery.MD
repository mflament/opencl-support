###User
* What we know about a user
* Edited by admin
* Use a simple data source from JSON for the current user repository, no need to edit or create admin interface
```java
class User {
  String login; // the id, must be unique, a Map would be enough for unicity
  String password; /// "123" harcoded for every users is fine :-)
  Locale locale;
}
```
Use [language tag](https://www.oracle.com/java/technologies/javase/jdk8-jre8-suported-locales.html#util-text) to exchange with client.

###For each API request (/api/*),
* the user must be authenticated
* the [Accept-Language](https://developer.mozilla.org/en-US/docs/Web/HTTP/Headers/Accept-Language) header must be supported
  * used if present
  * fallback to the user locale if not (but it's always present from a browser, use postman to remove it)

## Delivery
Business object edited in form.io (the subject). It's a simple delivery requested by a user, at a given date to a given place. 
```java
class Delivery {
    UUID id; // nullable, serialized (as string), not visible, not editable (set by the server on first persist, sent back to client for api reference)
    String title; // mandatory (not empty), max size: 128, visible, editable (obviously)
    String description; // string, not mandatory, max size: 1024, visible, editable
    Address address; //
    LocalDate dueDate; // mandatory, visible, editable, 'dynamic' minimum (see validation)
}

class Address {
    String street; // string, mandatory, visible, editable, max size: 256
    String countryId; // a single choice from api /api/country, mandatory
    String stateId; // a single from api /api/country/{countryId}/state/
}

// result of GET:/api/country (return a Set<Country>, sort is done client side on name)
class Country {
    String id;
    // user readable name (depending on the request/user locale, some backend i18n here)
    String name; 
}

// result of GET:/api/country/{countryId}/state/ (return a List<State>
class State {
  String id;
  // the name of the state, in the lang of the country (so all states have a single name, since they have a single country, no i18n here)
  String name; 
}
```
* Country id use [ISO 3166-1 alpha-2 code](https://en.wikipedia.org/wiki/ISO_3166-1_alpha-2#Officially_assigned_code_elements).   
* If the country has no state (empty list returned by api), the state should not be visible, (and so is not mandatory anymore)
### Due date validation
* the due date has a minimum date (can not be before this minimum)
* the minimum date is comptued server side:
  * the minimum due date is at least N (configurable in spring application.yaml) days in the future
  * the weekend, depending on the [address#country](https://en.wikipedia.org/wiki/Workweek_and_weekend), MUST BE RESPECTED (we are not Amazon !)
  * if there are some weekend days in this country between now and now + N days, this adds up to the minimum date

### TODO:
#### front
Check if the min date value can be requested dynamically by form.io, avoiding any customization of the json server side.
####back
If form.io can not request the min date, create the customization service:
* stop serving the form.json as a static resource
* create a controller able to customize the form.json from :
* the current user (not used right now, but it must be known, can be passed as a controller method parameter since he must be authenticated)
* a Delivery JSON object sent by the client, representing the current state
(required to know the address#country).
* If the country is not known yet (null on Delivery) the min date is simply now N + days.
* and of course, the current time of day