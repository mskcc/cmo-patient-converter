# cmo-patient-converter

Cmo Patient Converter is a Web Service providing enpoint to map mrn to Cmo Patient id.
Underneath it retrieves CRDB Patient id which is converted into CMO Patient id format.

To retrieve CMO Patient it:

call endpoint: /patient/{mrn}, eg: /patient/123456789

In case of success CMO patient id will be returned as a response
In case of client error (eg. mrn not existent) 400 Http Status will be returned with error message as a response 
in case of server error 500 Http Status will be returned with error message as a response
