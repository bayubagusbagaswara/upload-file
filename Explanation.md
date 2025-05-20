# Billing Service


- Bagaimana penanda customer untuk menentukan bahwa customer ini adalah Fund, Core, atau Retail
- Jika termasuk customer Core, maka perlu penanda juga untuk type berapa

# Read File Excel

- membaca file excel KSEI Fee
- Ambil hanya beberapa kolom / cell


```json

[
  {
    "id": "1",
    "product": "FR001",
    "date": "2023-11-29"
  },
  {
    "id": "2",
    "product": "FR002",
    "date": "2023-11-29"
   },
   {
    "id": "3",
    "product": "FR001",
    "date": "2023-11-30"
   },
   {
    "id": "4",
    "product": "FR002",
    "date": "2023-11-30"
   }
]

```

## Generate Billing Core
- Trigger dari depan hanya mengirim type "Core" dan period "Nov 2023"
- Jadi di belakang, harusnya akan men-generate semua customer tipe core (type 1 - 11)

# Format File Billing

- Customer Code"_"Jenis (Fund/Retail/Core)"_"periode (mmyyyy)
- Customer Code adalah AID

# Account4
- Format = GL 812017.0000 CC 0706
- 812017 from field Account
- 0706 from field Cost Center

# Retail Type 4
- setiap throw exception pasti langsung keluar process, tapi data yg diawal bisa kesimpan

@Query("select case when count(c)> 0 then true else false end from BillingCustomer c where lower(c.customerCode) = lower(:customerCode) AND lower(COALESCE(c.subCode,'') = lower(COALESCE(:subCode, ''))")
boolean existsCustomerByCustomerCodeAndSubCode
(@Param("customerCode") String customerCode, @Param("subCode") String subCode);

PROSES INSERT DATA BILLING CUSTOMER TO DATABASE
1. Validation data cannot be empty, and numeric or alphabetic
2. Validation data enum
3. Validation Investment Management Code is existing
4. Validation Customer Code is already taken

select gl_credit_account_value, gl_credit_name from billing_gl_credit where gl_credit_name = 'GL Safekeeping Fee' and gl_billing_template = 'CORE_TEMPLATE_7'

Accrual Biaya Kustodian
BI-SSSS( Transaction )+ KSEI Fee ( Transaction )
Biaya Penyelesaian Transaksi + Biaya Pihak ketiga 
Biaya Penyimpanan 
Biaya S4+Biaya KSEI 
Biaya Transfer 
GL Dana Custodian
GL HAK Operasional
GL Safekeeping Fee
KSEI Fee (Transaction) 
Safekeeping Fee
Safekeeping Fee + KSEI Fee 
TAX/VAT
Transaction Handling
Transaction Handling + Transaction Handling Internal
VAT 

## Insert Data Billing Fee Parameter
```sql
INSERT INTO billing_fee_param(fee_code, fee_name, fee_value, fee_description) 
VALUES ('F001', 'VAT', 11),
      ('F002', 'BI-SSSS', 23000),
      ('F003', 'KSEI', 22200),
      ('F004', 'SECURITY_AGENT', 0),
      ('F005', 'OTHER', 0),
      ('F006', 'AD_HOC_REPORT', 5000),
      ('F007', 'SECURITY_AGENT', 0),
      ('F008', 'THIRD_PARTY', 23000),
      ('F009', 'TRANSACTION_HANDLING_INTERNAL', 10),
      ('F010', 'SETTLEMENT_MINIMUM_CTBC_USD', 200000),
      ('F011', 'FEE_TRANSFER', 50000)

```

## IIG Data

- Alam Manunggal
```json
{
  "customerCode": "IIGAM",
  "customerName": "PT Alam Manunggal",
  "totalHolding": "16139582231",
  "priceTRUB": "50"
}
```

- Indo Infrastruktur
```json
{
  "customerCode": "IIGII",
  "customerName": "PT Indo Infrastruktur",
  "totalHolding": "832268145",
  "priceTRUB": "50"
}
```

- Mandala Kapital
```json
{
  "customerCode": "IIGMK",
  "customerName": "PT Mandala Kapital",
  "totalHolding": "4419235000",
  "priceTRUB": "50"
}
```

# Mapping data between Template and Type

CORE_TEMPLATE_3 => type_1 => daily
CORE_TEMPLATE_3 => type_2 => daily
CORE_TEMPLATE_7 => type_3 => daily
CORE_TEMPLATE_3 => type_4 => daily (17OBAL => ITAMA)
CORE_TEMPLATE_5 => type_4 => daily (17OBAL => EB)
CORE_TEMPLATE_1 => type_5 => daily (without npwp)
CORE_TEMPLATE_4 => type_5 => daily (with npwp)
CORE_TEMPLATE_1 => type_6 => daily (without npwp)
CORE_TEMPLATE_4 => type_6 => daily (with npwp)
CORE_TEMPLATE_2 => type_7 => monthly
CORE_TEMPLATE_6 => type_8 => daily (IIG)
CORE_TEMPLATE_3 => type_9 => monthly (KONI)
CORE_TEMPLATE_8 => type_10 => monthly (Treasury)
CORE_TEMPLATE_3 => type_11 => urun dana

TYPE_4 = ksei safe code mandatory
TYPE_5 = ksei safe code mandatory
TYPE_6 = ksei safe code mandatory
TYPE_7 = ksei safe code mandatory
-- ksei safe code must be exists in type_4, type_5, type_6, type_7. If ksei safe code blank, then throw validation error

ALTER TABLE accounts
MODIFY COLUMN balance DECIMAL(18, 4);

# CORE TEMPLATE
CORE_TEMPLATE_1 : No VAT (Safekeeping Fee, KSEI, etc) - type 5, type 6 (without NPWP)
CORE_TEMPLATE_2 : VAT (Foreign Client) - type 7
CORE_TEMPLATE_3 : VAT (Safekeeping Fee & Trx Handling) - type 1, type 2, type 4 a, type 9, type 11
CORE_TEMPLATE_4 : VAT (Safekeeping Fee, KSEI, etc) - type 5, type 6 (With NPWP)
CORE_TEMPLATE_5 : 17 OBAL (EB) - type 4b
CORE_TEMPLATE_6 : IIG - type 8
CORE_TEMPLATE_7 : No VAT (only Safekeeping Fee) - Type 3
CORE_TEMPLATE_8 : No VAT (only Trx Handling and Safekeeping Fee) Type 10

-- Yang ada sub total karena ada perhitungan VAT
-- TYPE 3 tidak ada sub_total (masih NULL) (karena hanya sebaris)
-- TYPE 10 tidak ada sub_total (masih NULL) (karena tidak ada perhitungan VAT)
-- TYPE 5  tidak ada sub_total (tidak ada VAT) (ini yg without NPWP)

# Settle gefu
private static final String SETTLE_DATE_STR1 = "20241231";
private static final String SETTLE_DATE_STR2 = "20241231";

# Tables Name
- instruksi_sinvest (done)
- master_product_rekening_debit / DebitAccountNumber (done)
- master_bank (done)
- placement_data (done)
- summary_trx (done)
- report_status (done)
- data_change / PlacementDepositDataChange (done)
- remaining_data
- ncbs_request
- ncbs_response

diawali kata placement_

# Models Name Placement
- DebitAccountNumber
- InstructionSInvest
- MasterBank
- NCBSRequest
- NCBSResponse
- PlacementData
- PlacementDataChange
- RemainingData
- ReportStatus
- SummaryTransaction

# Request Placement Deposit (create bulk)
```json
{
  "inputId": "v000111",
  "createBulkPlacementRequestList": [
    {}, {}, {}
  ]
}

```

# Step Inquiry Account

        // Step 1: Serialize the request object to a JSON string
        String jsonRequestBody = objectMapper.writeValueAsString(request);

        // Step 2: Remove all whitespace characters from the JSON string
        String processedRequestBody = jsonRequestBody.replaceAll("\\s", "");

        // Step 3: Generate the current timestamp in ISO 8601 format with UTC+7 offset
        String currentTimestamp = getCurrentTimestamp();

        // Step 4: Concatenate the timestamp and the processed request body
        String dataToSign = currentTimestamp + processedRequestBody;

        // Step 5: Compute the HMAC-SHA512 signature
        String signature = computeHMACSHA512(dataToSign, secretKey);

# Request JSON

```json
{
  "xferInfoFrom": {
  "acctId": "903600415859",
  "acctType": "26",
  "acctCur": "360",
  "bdiAcctStatus": " Y",
  "bdiAcctCitizen": " Y"
  }, 
  "bdiXferXFOff": {
    "bdiXferAmtFrm": "1000000000", 
    "bdiXferAmtFrmLCE": "1000000000", 
    "bdiXferAmtTo": "1000000000", 
    "bdiXferAmtToLCE": "1000000000", 
    "bdiXferType": "4", 
    "bdiXferCurCode": "0", 
    "bdiXRateAmt": "100", 
    "bdiStdRateAmt": "100", 
    "bdiXReffNumber": "", 
    "bdiXferBeneficiery": {
      "bdiBenfID": " ",
      "bdiBenfAcct": "29895023100",
      "bdiBenfName": "ASTRI BUDIARTI",
      "bdiBenfAddress": " ",
      "bdiBenStatus": "Y",
      "bdiBenCitizen": "Y",
      "bankInfo": {
        "biCode": "1100019",
        "cocCode": "901",
        "name": "Jakarta "
      }
    }, 
    "bdiXferCostCtr": "7101",
    "bdiFeeAmt": "3500000",
    "bdiFeeAmtLCE": "3500000",
    "bdiFeeProcIr": "S",
    "bdiXferMemo": {
      "bdiFrMemo1": "Internet Trf dari AUDI MAULANA", 
      "bdiFrMemo2": "ke ASTRI BUDIARTI 11", 
      "bdiToMemo1": "ke ASTRI BUDIARTI 11", 
      "bdiToMemo2": "Internet Trf dari AUDI MAULANA"
    }, 
    "transInfo": {
      "trn": "IFT00000", 
      "fee": " BEN"
    }, 
    "lldInfo": "ID015"
  }
}


```

# Contoh Request Approve

```java 
@Autowired
private TransactionFailureLogRepository transactionFailureLogRepository;

public ApprovalResponseDTO approve(Long placementDepositApprovalId, String approveId, String approveIPAddress) {
    log.info("Start approve placement approval with id: {}, approveId: {}, and approveIPAddress: {}", 
             placementDepositApprovalId, approveId, approveIPAddress);

    ApprovalResponseDTO responseDTO = new ApprovalResponseDTO();

    PlacementApproval placementApproval = placementDepositApprovalRepository.findById(placementDepositApprovalId)
            .orElseThrow(() -> new DataNotFoundException("Placement Approval not found with id: " + placementDepositApprovalId));

    placementApproval.setApprovalStatus(ApprovalStatus.Approved);
    placementApproval.setApproverId(approveId);
    placementApproval.setApproveIPAddress(approveIPAddress);
    placementApproval.setApproveDate(LocalDateTime.now());

    PlacementApproval placementApprovalSaved = placementDepositApprovalRepository.save(placementApproval);
    log.info("Save approve placement approval: {}", placementApprovalSaved);

    // **Step 1: Inquiry Account**
    InquiryAccountRequest inquiryAccountRequest = createInquiryAccountRequest(placementApprovalSaved);
    InquiryAccountResponse inquiryAccountResponse = ncbsRequestService.inquiryAccount(inquiryAccountRequest);

    if ("000".equals(inquiryAccountResponse.getResponseCode())) {
        responseDTO.incrementSuccess();
    } else {
        responseDTO.incrementFailed("INQUIRY_ACCOUNT", inquiryAccountResponse.getResponseCode(),
                "Inquiry Account failed with response code: " + inquiryAccountResponse.getResponseCode());
        transactionFailureLogRepository.save(new TransactionFailureLog(
                placementDepositApprovalId, 
                "INQUIRY_ACCOUNT",
                inquiryAccountResponse.getResponseCode(),
                "Inquiry Account failed"
        ));
    }

    // **Step 2: Placement Type Handling**
    if (PLACEMENT_TYPE_INTERNAL.equalsIgnoreCase(placementApprovalSaved.getPlacementType())) {
        // **Overbooking Casa**
        OverbookingCasaRequest overbookingCasaRequest = createOverbookingCasaRequest(placementApprovalSaved);
        OverbookingCasaResponse overbookingCasaResponse = ncbsRequestService.overbookingCasa(overbookingCasaRequest);

        if ("000".equals(overbookingCasaResponse.getResponseCode())) {
            responseDTO.incrementSuccess();
        } else {
            responseDTO.incrementFailed("OVERBOOKING_CASA", overbookingCasaResponse.getResponseCode(),
                    "Overbooking Casa failed with response code: " + overbookingCasaResponse.getResponseCode());
            transactionFailureLogRepository.save(new TransactionFailureLog(
                    placementDepositApprovalId,
                    "OVERBOOKING_CASA",
                    overbookingCasaResponse.getResponseCode(),
                    "Overbooking Casa failed"
            ));
        }
    } else if (PLACEMENT_TYPE_EXTERNAL.equalsIgnoreCase(placementApprovalSaved.getPlacementType())) {
        log.info("Processing external placement approval");
        responseDTO.incrementSuccess();
    } else {
        log.warn("Placement type not recognized: {}", placementApprovalSaved.getPlacementType());
        responseDTO.incrementFailed("UNKNOWN_TYPE", "999", "Placement type not recognized: " + placementApprovalSaved.getPlacementType());
    }

    return responseDTO;
}

```
# Contoh Response JSON

```json
{
    "totalDataSuccess": 1,
    "totalDataFailed": 1,
    "errorMessageDTOList": [
        {
            "processName": "INQUIRY_ACCOUNT",
            "responseCode": "001",
            "message": "Inquiry Account failed with response code: 001"
        }
    ]
}

```

# Contoh Approval Response DTO

```java
public class ApprovalResponseDTO {
    private Integer totalDataSuccess = 0;
    private Integer totalDataFailed = 0;
    private List<ErrorMessageDTO> errorMessageDTOList = new ArrayList<>();

    public void incrementSuccess() {
        this.totalDataSuccess++;
    }

    public void incrementFailed(String processName, String responseCode, String message) {
        this.totalDataFailed++;
        this.errorMessageDTOList.add(new ErrorMessageDTO(processName, responseCode, message));
    }

    // Getters & Setters
    public Integer getTotalDataSuccess() {
        return totalDataSuccess;
    }

    public Integer getTotalDataFailed() {
        return totalDataFailed;
    }

    public List<ErrorMessageDTO> getErrorMessageDTOList() {
        return errorMessageDTOList;
    }
}

```

# Update Placement Approval Service

```java
public ApproveResponseDTO approve(Long placementDepositApprovalId, String approveId, String approveIPAddress) {
    log.info("Start approve placement approval with id: {}, approveId: {}, and approveIPAddress: {}", 
             placementDepositApprovalId, approveId, approveIPAddress);

    PlacementApproval placementApproval = placementApprovalRepository.findById(placementDepositApprovalId)
            .orElseThrow(() -> new DataNotFoundException("Placement Approval not found with id: " + placementDepositApprovalId));

    placementApproval.setApprovalStatus(ApprovalStatus.Approved);
    placementApproval.setApproverId(approveId);
    placementApproval.setApproveIPAddress(approveIPAddress);
    placementApproval.setApproveDate(LocalDateTime.now());

    placementApprovalRepository.save(placementApproval);
    log.info("Saved approve placement approval: {}", placementApproval);

    int totalSuccess = 0;
    int totalFailed = 0;
    List<ErrorMessageDTO> errorMessagesList = new ArrayList<>();

    // **Step 1: Inquiry Account**
    InquiryAccountRequest inquiryAccountRequest = createInquiryAccountRequest(placementApproval);
    InquiryAccountResponse inquiryAccountResponse = ncbsRequestService.inquiryAccount(inquiryAccountRequest);

    if (!"000".equals(inquiryAccountResponse.getResponseCode())) {
        totalFailed++;
        errorMessagesList.add(ErrorMessageDTO.builder()
                .code(inquiryAccountResponse.getResponseCode())
                .errorMessages(Collections.singletonList("Inquiry Account failed with response code: " + inquiryAccountResponse.getResponseCode()))
                .build());
    } else {
        totalSuccess++;

        // **Step 2: Overbooking Casa (Jika placementType = INTERNAL)**
        if (PLACEMENT_TYPE_INTERNAL.equalsIgnoreCase(placementApproval.getPlacementType())) {
            OverbookingCasaRequest overbookingCasaRequest = createOverbookingCasaRequest(placementApproval);
            OverbookingCasaResponse overbookingCasaResponse = ncbsRequestService.overbookingCasa(overbookingCasaRequest);

            if (!"000".equals(overbookingCasaResponse.getResponseCode())) {
                totalFailed++;
                errorMessagesList.add(ErrorMessageDTO.builder()
                        .code(overbookingCasaResponse.getResponseCode())
                        .errorMessages(Collections.singletonList("Overbooking Casa failed with response code: " + overbookingCasaResponse.getResponseCode()))
                        .build());
            } else {
                totalSuccess++;
            }
        } else if (PLACEMENT_TYPE_EXTERNAL.equalsIgnoreCase(placementApproval.getPlacementType())) {
            log.info("Processing external placement: SKN, RTGS, BI-FAST");
        } else {
            log.info("Placement type not recognized");
        }
    }

    return ApproveResponseDTO.builder()
            .totalDataSuccess(totalSuccess)
            .totalDataFailed(totalFailed)
            .errorMessageDTOList(errorMessagesList)
            .build();
}
```

# Update

```java
public ApproveResponseDTO approve(Long placementDepositApprovalId, String approveId, String approveIPAddress) {
    log.info("Start approve placement approval with id: {}, approveId: {}, and approveIPAddress: {}", 
             placementDepositApprovalId, approveId, approveIPAddress);

    PlacementApproval placementApproval = placementDepositApprovalRepository.findById(placementDepositApprovalId)
            .orElseThrow(() -> new DataNotFoundException("Placement Approval not found with id: " + placementDepositApprovalId));

    placementApproval.setApprovalStatus(ApprovalStatus.Approved);
    placementApproval.setApproverId(approveId);
    placementApproval.setApproveIPAddress(approveIPAddress);
    placementApproval.setApproveDate(LocalDateTime.now());

    placementDepositApprovalRepository.save(placementApproval);
    log.info("Saved approve placement approval: {}", placementApproval);

    int totalSuccess = 0;
    int totalFailed = 0;
    List<ErrorMessageDTO> errorMessagesList = new ArrayList<>();

    // **Step 1: Inquiry Account**
    InquiryAccountRequest inquiryAccountRequest = createInquiryAccountRequest(placementApproval);
    InquiryAccountResponse inquiryAccountResponse = ncbsRequestService.inquiryAccount(inquiryAccountRequest);

    if (!"000".equals(inquiryAccountResponse.getResponseCode())) {
        totalFailed++;
        errorMessagesList.add(ErrorMessageDTO.builder()
                .code(inquiryAccountResponse.getResponseCode())
                .errorMessages(Collections.singletonList("Inquiry Account failed with response code: " + inquiryAccountResponse.getResponseCode()))
                .build());

        // **Stop Proses Jika Inquiry Gagal**
        return ApproveResponseDTO.builder()
                .totalDataSuccess(totalSuccess)
                .totalDataFailed(totalFailed)
                .errorMessageDTOList(errorMessagesList)
                .build();
    }

    // **Jika Inquiry Sukses, lanjut ke proses Overbooking Casa atau SKN, RTGS, BI-FAST**
    if (PLACEMENT_TYPE_INTERNAL.equalsIgnoreCase(placementApproval.getPlacementType())) {
        // **Step 2: Overbooking Casa**
        OverbookingCasaRequest overbookingCasaRequest = createOverbookingCasaRequest(placementApproval);
        OverbookingCasaResponse overbookingCasaResponse = ncbsRequestService.overbookingCasa(overbookingCasaRequest);

        if (!"000".equals(overbookingCasaResponse.getResponseCode())) {
            totalFailed++;
            errorMessagesList.add(ErrorMessageDTO.builder()
                    .code(overbookingCasaResponse.getResponseCode())
                    .errorMessages(Collections.singletonList("Overbooking Casa failed with response code: " + overbookingCasaResponse.getResponseCode()))
                    .build());
        } else {
            totalSuccess++; // **Increment Success hanya jika Overbooking sukses**
        }
    } else if (PLACEMENT_TYPE_EXTERNAL.equalsIgnoreCase(placementApproval.getPlacementType())) {
        // **Step 2: SKN, RTGS, BI-FAST**
        CreditTransferRequest creditTransferRequest = createCreditTransferRequest(placementApproval);
        CreditTransferResponse creditTransferResponse = ncbsRequestService.creditTransfer(creditTransferRequest);

        if (!"000".equals(creditTransferResponse.getResponseCode())) {
            totalFailed++;
            errorMessagesList.add(ErrorMessageDTO.builder()
                    .code(creditTransferResponse.getResponseCode())
                    .errorMessages(Collections.singletonList("Credit Transfer failed with response code: " + creditTransferResponse.getResponseCode()))
                    .build());
        } else {
            totalSuccess++; // **Increment Success hanya jika Credit Transfer sukses**
        }
    } else {
        log.info("Placement type not recognized");
    }

    return ApproveResponseDTO.builder()
            .totalDataSuccess(totalSuccess)
            .totalDataFailed(totalFailed)
            .errorMessageDTOList(errorMessagesList)
            .build();
}

```

# Update

```java
public ApproveResponseDTO approve(Long placementDepositApprovalId, String approveId, String approveIPAddress) {
    log.info("Start approve placement approval with id: {}, approveId: {}, and approveIPAddress: {}", 
             placementDepositApprovalId, approveId, approveIPAddress);

    PlacementApproval placementApproval = placementDepositApprovalRepository.findById(placementDepositApprovalId)
            .orElseThrow(() -> new DataNotFoundException("Placement Approval not found with id: " + placementDepositApprovalId));

    placementApproval.setApprovalStatus(ApprovalStatus.Approved);
    placementApproval.setApproverId(approveId);
    placementApproval.setApproveIPAddress(approveIPAddress);
    placementApproval.setApproveDate(LocalDateTime.now());

    placementDepositApprovalRepository.save(placementApproval);
    log.info("Saved approve placement approval: {}", placementApproval);

    int totalSuccess = 0;
    int totalFailed = 0;
    List<ErrorMessageDTO> errorMessagesList = new ArrayList<>();

    // **Step 1: Inquiry Account**
    InquiryAccountRequest inquiryAccountRequest = createInquiryAccountRequest(placementApproval);
    InquiryAccountResponse inquiryAccountResponse = ncbsRequestService.inquiryAccount(inquiryAccountRequest);

    if (!"000".equals(inquiryAccountResponse.getResponseCode())) {
        totalFailed++;
        errorMessagesList.add(ErrorMessageDTO.builder()
                .code(inquiryAccountResponse.getResponseCode())
                .errorMessages(Collections.singletonList("Inquiry Account failed with response code: " + inquiryAccountResponse.getResponseCode()))
                .build());

        return ApproveResponseDTO.builder()
                .totalDataSuccess(totalSuccess)
                .totalDataFailed(totalFailed)
                .errorMessageDTOList(errorMessagesList)
                .build();
    }

    // **Step 2: Overbooking Casa atau Credit Transfer**
    if (PLACEMENT_TYPE_INTERNAL.equalsIgnoreCase(placementApproval.getPlacementType())) {
        // **Overbooking Casa**
        OverbookingCasaRequest overbookingCasaRequest = createOverbookingCasaRequest(placementApproval);
        OverbookingCasaResponse overbookingCasaResponse = ncbsRequestService.overbookingCasa(overbookingCasaRequest);

        String overbookingResponseCode = overbookingCasaResponse.getResponseCode();
        if (!"000".equals(overbookingResponseCode)) {
            totalFailed++;
            errorMessagesList.add(getOverbookingErrorMessage(overbookingResponseCode));
        } else {
            totalSuccess++;
        }
    } else if (PLACEMENT_TYPE_EXTERNAL.equalsIgnoreCase(placementApproval.getPlacementType())) {
        // **Credit Transfer (SKN, RTGS, BI-FAST)**
        CreditTransferRequest creditTransferRequest = createCreditTransferRequest(placementApproval);
        CreditTransferResponse creditTransferResponse = ncbsRequestService.creditTransfer(creditTransferRequest);

        String creditTransferResponseCode = creditTransferResponse.getResponseCode();
        if (!"000".equals(creditTransferResponseCode)) {
            totalFailed++;
            errorMessagesList.add(getCreditTransferErrorMessage(creditTransferResponseCode));
        } else {
            totalSuccess++;
        }
    } else {
        log.info("Placement type not recognized");
    }

    return ApproveResponseDTO.builder()
            .totalDataSuccess(totalSuccess)
            .totalDataFailed(totalFailed)
            .errorMessageDTOList(errorMessagesList)
            .build();
}

```

# Error Message DTO

```java
private ErrorMessageDTO getOverbookingErrorMessage(String responseCode) {
    Map<String, String> errorMessages = new HashMap<>();
    errorMessages.put("101", "Insufficient balance for Overbooking Casa");
    errorMessages.put("102", "Invalid account number");
    errorMessages.put("103", "Overbooking amount exceeds limit");
    errorMessages.put("104", "Account is locked or restricted");
    errorMessages.put("999", "Unknown error during Overbooking Casa");

    String message = errorMessages.getOrDefault(responseCode, "Unexpected error with response code: " + responseCode);
    
    return ErrorMessageDTO.builder()
            .code(responseCode)
            .errorMessages(Collections.singletonList(message))
            .build();
}

private ErrorMessageDTO getCreditTransferErrorMessage(String responseCode) {
    Map<String, String> errorMessages = new HashMap<>();
    errorMessages.put("201", "Beneficiary account not found");
    errorMessages.put("202", "Transfer amount exceeds daily limit");
    errorMessages.put("203", "Invalid transaction type for selected bank");
    errorMessages.put("204", "Transaction blocked due to compliance reasons");
    errorMessages.put("999", "Unknown error during Credit Transfer");

    String message = errorMessages.getOrDefault(responseCode, "Unexpected error with response code: " + responseCode);
    
    return ErrorMessageDTO.builder()
            .code(responseCode)
            .errorMessages(Collections.singletonList(message))
            .build();
}

```

# Error Message Util

```java
import lombok.Builder;
import lombok.Data;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ErrorMessageUtil {

    private static final Map<String, String> OVERBOOKING_ERROR_MESSAGES = new HashMap<>();
    private static final Map<String, String> CREDIT_TRANSFER_ERROR_MESSAGES = new HashMap<>();

    static {
        // Error messages for Overbooking Casa
        OVERBOOKING_ERROR_MESSAGES.put("101", "Insufficient balance for Overbooking Casa");
        OVERBOOKING_ERROR_MESSAGES.put("102", "Invalid account number");
        OVERBOOKING_ERROR_MESSAGES.put("103", "Overbooking amount exceeds limit");
        OVERBOOKING_ERROR_MESSAGES.put("104", "Account is locked or restricted");
        OVERBOOKING_ERROR_MESSAGES.put("999", "Unknown error during Overbooking Casa");

        // Error messages for Credit Transfer (SKN, RTGS, BI-FAST)
        CREDIT_TRANSFER_ERROR_MESSAGES.put("201", "Beneficiary account not found");
        CREDIT_TRANSFER_ERROR_MESSAGES.put("202", "Transfer amount exceeds daily limit");
        CREDIT_TRANSFER_ERROR_MESSAGES.put("203", "Invalid transaction type for selected bank");
        CREDIT_TRANSFER_ERROR_MESSAGES.put("204", "Transaction blocked due to compliance reasons");
        CREDIT_TRANSFER_ERROR_MESSAGES.put("999", "Unknown error during Credit Transfer");
    }

    public static ErrorMessageDTO getOverbookingErrorMessage(String responseCode) {
        String message = OVERBOOKING_ERROR_MESSAGES.getOrDefault(responseCode, 
                "Unexpected error with response code: " + responseCode);
        return buildErrorMessage(responseCode, message);
    }

    public static ErrorMessageDTO getCreditTransferErrorMessage(String responseCode) {
        String message = CREDIT_TRANSFER_ERROR_MESSAGES.getOrDefault(responseCode, 
                "Unexpected error with response code: " + responseCode);
        return buildErrorMessage(responseCode, message);
    }

    private static ErrorMessageDTO buildErrorMessage(String responseCode, String message) {
        return ErrorMessageDTO.builder()
                .code(responseCode)
                .errorMessages(Collections.singletonList(message))
                .build();
    }
}
```

# Approval Service

```java
import java.util.ArrayList;
import java.util.List;

public ApproveResponseDTO approve(Long placementDepositApprovalId, String approveId, String approveIPAddress) {
    int totalSuccess = 0;
    int totalFailed = 0;
    List<ErrorMessageDTO> errorMessagesList = new ArrayList<>();

    // **Step 1: Inquiry Account**
    InquiryAccountResponse inquiryAccountResponse = ncbsRequestService.inquiryAccount(createInquiryAccountRequest());
    if (!"000".equals(inquiryAccountResponse.getResponseCode())) {
        totalFailed++;
        errorMessagesList.add(ErrorMessageUtil.getOverbookingErrorMessage(inquiryAccountResponse.getResponseCode()));

        return ApproveResponseDTO.builder()
                .totalDataSuccess(totalSuccess)
                .totalDataFailed(totalFailed)
                .errorMessageDTOList(errorMessagesList)
                .build();
    }

    // **Step 2: Overbooking Casa atau Credit Transfer**
    if (PLACEMENT_TYPE_INTERNAL.equalsIgnoreCase(placementType)) {
        OverbookingCasaResponse overbookingCasaResponse = ncbsRequestService.overbookingCasa(createOverbookingCasaRequest());
        if (!"000".equals(overbookingCasaResponse.getResponseCode())) {
            totalFailed++;
            errorMessagesList.add(ErrorMessageUtil.getOverbookingErrorMessage(overbookingCasaResponse.getResponseCode()));
        } else {
            totalSuccess++;
        }
    } else if (PLACEMENT_TYPE_EXTERNAL.equalsIgnoreCase(placementType)) {
        CreditTransferResponse creditTransferResponse = ncbsRequestService.creditTransfer(createCreditTransferRequest());
        if (!"000".equals(creditTransferResponse.getResponseCode())) {
            totalFailed++;
            errorMessagesList.add(ErrorMessageUtil.getCreditTransferErrorMessage(creditTransferResponse.getResponseCode()));
        } else {
            totalSuccess++;
        }
    }

    return ApproveResponseDTO.builder()
            .totalDataSuccess(totalSuccess)
            .totalDataFailed(totalFailed)
            .errorMessageDTOList(errorMessagesList)
            .build();
}

```

# Approval Service

```java
import java.util.ArrayList;
import java.util.List;

public ApproveResponseDTO approve(Long placementDepositApprovalId, String approveId, String approveIPAddress) {
    int totalSuccess = 0;
    int totalFailed = 0;
    List<ErrorMessageDTO> errorMessagesList = new ArrayList<>();

    // **Step 1: Inquiry Account**
    InquiryAccountResponse inquiryAccountResponse = ncbsRequestService.inquiryAccount(createInquiryAccountRequest());
    if (!"000".equals(inquiryAccountResponse.getResponseCode())) {
        totalFailed++;
        errorMessagesList.add(ErrorMessageUtil.getInquiryAccountErrorMessage(inquiryAccountResponse.getResponseCode()));

        return ApproveResponseDTO.builder()
                .totalDataSuccess(totalSuccess)
                .totalDataFailed(totalFailed)
                .errorMessageDTOList(errorMessagesList)
                .build();
    }

    // **Step 2: Overbooking Casa atau Credit Transfer**
    if (PLACEMENT_TYPE_INTERNAL.equalsIgnoreCase(placementType)) {
        OverbookingCasaResponse overbookingCasaResponse = ncbsRequestService.overbookingCasa(createOverbookingCasaRequest());
        if (!"000".equals(overbookingCasaResponse.getResponseCode())) {
            totalFailed++;
            errorMessagesList.add(ErrorMessageUtil.getOverbookingErrorMessage(overbookingCasaResponse.getResponseCode()));
        } else {
            totalSuccess++;
        }
    } else if (PLACEMENT_TYPE_EXTERNAL.equalsIgnoreCase(placementType)) {
        CreditTransferResponse creditTransferResponse = ncbsRequestService.creditTransfer(createCreditTransferRequest());
        if (!"000".equals(creditTransferResponse.getResponseCode())) {
            totalFailed++;
            errorMessagesList.add(ErrorMessageUtil.getCreditTransferErrorMessage(creditTransferResponse.getResponseCode()));
        } else {
            totalSuccess++;
        }
    }

    return ApproveResponseDTO.builder()
            .totalDataSuccess(totalSuccess)
            .totalDataFailed(totalFailed)
            .errorMessageDTOList(errorMessagesList)
            .build();
}
```

# Error Message Util

```java
import lombok.Builder;
import lombok.Data;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ErrorMessageUtil {

    private static final Map<String, String> INQUIRY_ACCOUNT_ERROR_MESSAGES = new HashMap<>();
    private static final Map<String, String> OVERBOOKING_ERROR_MESSAGES = new HashMap<>();
    private static final Map<String, String> CREDIT_TRANSFER_ERROR_MESSAGES = new HashMap<>();

    static {
        // Error messages for Inquiry Account
        INQUIRY_ACCOUNT_ERROR_MESSAGES.put("001", "Account not found");
        INQUIRY_ACCOUNT_ERROR_MESSAGES.put("002", "Invalid account number format");
        INQUIRY_ACCOUNT_ERROR_MESSAGES.put("003", "Account is inactive or closed");
        INQUIRY_ACCOUNT_ERROR_MESSAGES.put("004", "System error during inquiry");
        INQUIRY_ACCOUNT_ERROR_MESSAGES.put("999", "Unknown error during Inquiry Account");

        // Error messages for Overbooking Casa
        OVERBOOKING_ERROR_MESSAGES.put("101", "Insufficient balance for Overbooking Casa");
        OVERBOOKING_ERROR_MESSAGES.put("102", "Invalid account number");
        OVERBOOKING_ERROR_MESSAGES.put("103", "Overbooking amount exceeds limit");
        OVERBOOKING_ERROR_MESSAGES.put("104", "Account is locked or restricted");
        OVERBOOKING_ERROR_MESSAGES.put("999", "Unknown error during Overbooking Casa");

        // Error messages for Credit Transfer (SKN, RTGS, BI-FAST)
        CREDIT_TRANSFER_ERROR_MESSAGES.put("201", "Beneficiary account not found");
        CREDIT_TRANSFER_ERROR_MESSAGES.put("202", "Transfer amount exceeds daily limit");
        CREDIT_TRANSFER_ERROR_MESSAGES.put("203", "Invalid transaction type for selected bank");
        CREDIT_TRANSFER_ERROR_MESSAGES.put("204", "Transaction blocked due to compliance reasons");
        CREDIT_TRANSFER_ERROR_MESSAGES.put("999", "Unknown error during Credit Transfer");
    }

    public static ErrorMessageDTO getInquiryAccountErrorMessage(String responseCode) {
        String message = INQUIRY_ACCOUNT_ERROR_MESSAGES.getOrDefault(responseCode,
                "Unexpected error with response code: " + responseCode);
        return buildErrorMessage(responseCode, message);
    }

    public static ErrorMessageDTO getOverbookingErrorMessage(String responseCode) {
        String message = OVERBOOKING_ERROR_MESSAGES.getOrDefault(responseCode, 
                "Unexpected error with response code: " + responseCode);
        return buildErrorMessage(responseCode, message);
    }

    public static ErrorMessageDTO getCreditTransferErrorMessage(String responseCode) {
        String message = CREDIT_TRANSFER_ERROR_MESSAGES.getOrDefault(responseCode, 
                "Unexpected error with response code: " + responseCode);
        return buildErrorMessage(responseCode, message);
    }

    private static ErrorMessageDTO buildErrorMessage(String responseCode, String message) {
        return ErrorMessageDTO.builder()
                .code(responseCode)
                .errorMessages(Collections.singletonList(message))
                .build();
    }
}

```

# Refactor Placement Approval Service

```java
public PlacementApprovalResponse approve(Long placementDepositApprovalId, String approveId, String approveIPAddress) {
    log.info("Start approve placement approval with id: {}, approveId: {}, and approveIPAddress: {}", placementDepositApprovalId, approveId, approveIPAddress);
    
    PlacementApproval placementApproval = getPlacementApprovalById(placementDepositApprovalId);
    updateApprovalDetails(placementApproval, approveId, approveIPAddress);
    
    int totalSuccess = 0;
    int totalFailed = 0;
    List<ErrorMessageDTO> errorMessageDTOList = new ArrayList<>();

    // Step 1: Inquiry Account
    if (!validateInquiryAccount(placementApproval, errorMessageDTOList)) {
        return buildPlacementApprovalResponse(totalSuccess, ++totalFailed, errorMessageDTOList);
    }

    // Step 2: Process Transfer
    if (PLACEMENT_TYPE_INTERNAL.equalsIgnoreCase(placementApproval.getPlacementType())) {
        totalSuccess += processInternalPlacement(placementApproval, errorMessageDTOList);
    } else if (PLACEMENT_TYPE_EXTERNAL.equalsIgnoreCase(placementApproval.getPlacementType())) {
        totalSuccess += processExternalPlacement(placementApproval, errorMessageDTOList);
    }

    return buildPlacementApprovalResponse(totalSuccess, totalFailed, errorMessageDTOList);
}

private PlacementApproval getPlacementApprovalById(Long id) {
    return placementApprovalRepository.findById(id)
            .orElseThrow(() -> new DataNotFoundException("Placement Approval not found with id: " + id));
}

private void updateApprovalDetails(PlacementApproval placementApproval, String approveId, String approveIPAddress) {
    placementApproval.setApprovalStatus(ApprovalStatus.Approved);
    placementApproval.setApproverId(approveId);
    placementApproval.setApproveIPAddress(approveIPAddress);
    placementApproval.setApproveDate(LocalDateTime.now());
    placementApprovalRepository.save(placementApproval);
}

private boolean validateInquiryAccount(PlacementApproval placementApproval, List<ErrorMessageDTO> errorMessageDTOList) {
    InquiryAccountRequest request = createInquiryAccountRequest(placementApproval);
    InquiryAccountResponse response = ncbsRequestService.inquiryAccount(request);
    
    if (!"000".equals(response.getResponseCode())) {
        errorMessageDTOList.add(ErrorMessageUtil.getInquiryAccountErrorMessage(response.getResponseCode(), response.getResponseMessage()));
        return false;
    }
    return true;
}

private int processInternalPlacement(PlacementApproval placementApproval, List<ErrorMessageDTO> errorMessageDTOList) {
    OverbookingCasaRequest request = createOverbookingCasaRequest(placementApproval);
    OverbookingCasaResponse response = ncbsRequestService.overbookingCasa(request);
    
    if (!"000".equals(response.getResponseCode())) {
        errorMessageDTOList.add(ErrorMessageUtil.getOverbookingCasaErrorMessage(response.getResponseCode(), response.getResponseMessage()));
        return 0;
    }
    return 1;
}

private int processExternalPlacement(PlacementApproval placementApproval, List<ErrorMessageDTO> errorMessageDTOList) {
    String transferType = placementApproval.getPlacementTransferType();
    
    if ("BI-FAST".equalsIgnoreCase(transferType)) {
        return processCreditTransferBiFast(placementApproval, errorMessageDTOList);
    } else if ("SKN".equalsIgnoreCase(transferType) || "RTGS".equalsIgnoreCase(transferType)) {
        return processTransferSknRtgs(placementApproval, errorMessageDTOList);
    }
    return 0;
}

private int processCreditTransferBiFast(PlacementApproval placementApproval, List<ErrorMessageDTO> errorMessageDTOList) {
    CreditTransferRequest request = createCreditTransferBiFastRequest(placementApproval);
    CreditTransferResponse response = ncbsRequestService.creditTransfer(request);
    
    if (!"000".equals(response.getResponseCode())) {
        errorMessageDTOList.add(ErrorMessageUtil.getCreditTransferBiFastErrorMessage(response.getResponseCode(), response.getResponseMessage()));
        return 0;
    }
    return 1;
}

private int processTransferSknRtgs(PlacementApproval placementApproval, List<ErrorMessageDTO> errorMessageDTOList) {
    TransferSknRtgsRequest request = createTransferSknRtgsRequest(placementApproval);
    TransferSknRtgsResponse response = ncbsRequestService.transferSknRtgs(request);
    
    if (!"000".equals(response.getResponseCode())) {
        errorMessageDTOList.add(ErrorMessageUtil.getCreditTransferBiFastErrorMessage(response.getResponseCode(), response.getResponseMessage()));
        return 0;
    }
    return 1;
}

private PlacementApprovalResponse buildPlacementApprovalResponse(int totalSuccess, int totalFailed, List<ErrorMessageDTO> errorMessageDTOList) {
    return PlacementApprovalResponse.builder()
            .totalDataSuccess(totalSuccess)
            .totalDataFailed(totalFailed)
            .errorMessageDTOList(errorMessageDTOList)
            .build();
}

```

# Approval Service

```java
@Service
@RequiredArgsConstructor
@Slf4j
public class PlacementApprovalService {

    private final PlacementApprovalRepository placementApprovalRepository;
    private final NcbsRequestService ncbsRequestService;
    private final PlacementTransactionLogRepository placementTransactionLogRepository;

    public PlacementApprovalResponse approve(Long placementDepositApprovalId, String approveId, String approveIPAddress) {
        log.info("Start approve placement approval with id: {}, approveId: {}, and approveIPAddress: {}", placementDepositApprovalId, approveId, approveIPAddress);
        
        PlacementApproval placementApproval = placementApprovalRepository.findById(placementDepositApprovalId)
                .orElseThrow(() -> new DataNotFoundException("Placement Approval not found with id: " + placementDepositApprovalId));

        placementApproval.setApprovalStatus(ApprovalStatus.Approved);
        placementApproval.setApproverId(approveId);
        placementApproval.setApproveIPAddress(approveIPAddress);
        placementApproval.setApproveDate(LocalDateTime.now());

        placementApprovalRepository.save(placementApproval);
        log.info("Saved approve placement approval: {}", placementApproval);

        int totalSuccess = 0;
        int totalFailed = 0;
        List<ErrorMessageDTO> errorMessageDTOList = new ArrayList<>();

        // **Step 1: Inquiry Account**
        InquiryAccountRequest inquiryAccountRequest = createInquiryAccountRequest(placementApproval);
        InquiryAccountResponse inquiryAccountResponse = ncbsRequestService.inquiryAccount(inquiryAccountRequest);

        if (!"000".equals(inquiryAccountResponse.getResponseCode())) {
            totalFailed++;
            errorMessageDTOList.add(ErrorMessageUtil.getInquiryAccountErrorMessage(
                    inquiryAccountResponse.getResponseCode(), inquiryAccountResponse.getResponseMessage()));
            return buildResponse(totalSuccess, totalFailed, errorMessageDTOList);
        }

        // **Step 2: Process Transfer Placement**
        if (PLACEMENT_TYPE_INTERNAL.equalsIgnoreCase(placementApproval.getPlacementType())) {
            totalSuccess += processInternalTransfer(placementApproval, errorMessageDTOList);
        } else if (PLACEMENT_TYPE_EXTERNAL.equalsIgnoreCase(placementApproval.getPlacementType())) {
            totalSuccess += processExternalTransfer(placementApproval, errorMessageDTOList);
        }

        return buildResponse(totalSuccess, totalFailed, errorMessageDTOList);
    }

    private int processInternalTransfer(PlacementApproval placementApproval, List<ErrorMessageDTO> errorMessageDTOList) {
        OverbookingCasaRequest overbookingCasaRequest = createOverbookingCasaRequest(placementApproval);
        OverbookingCasaResponse overbookingCasaResponse = ncbsRequestService.overbookingCasa(overbookingCasaRequest);

        if (!"000".equals(overbookingCasaResponse.getResponseCode())) {
            errorMessageDTOList.add(ErrorMessageUtil.getOverbookingCasaErrorMessage(
                    overbookingCasaResponse.getResponseCode(), overbookingCasaResponse.getResponseMessage()));
            return 0;
        }

        saveTransactionLog(placementApproval.getId(), "INTERNAL", overbookingCasaResponse.getResponseCode(), overbookingCasaResponse.getResponseMessage());
        return 1;
    }

    private int processExternalTransfer(PlacementApproval placementApproval, List<ErrorMessageDTO> errorMessageDTOList) {
        String placementTransferType = placementApproval.getPlacementTransferType();
        String transactionType = "EXTERNAL-" + placementTransferType;
        
        TransferResponse transferResponse;
        if ("BI-FAST".equalsIgnoreCase(placementTransferType)) {
            CreditTransferRequest request = createCreditTransferBiFastRequest(placementApproval);
            transferResponse = ncbsRequestService.creditTransfer(request);
        } else if ("SKN".equalsIgnoreCase(placementTransferType) || "RTGS".equalsIgnoreCase(placementTransferType)) {
            TransferSknRtgsRequest request = createTransferSknRtgsRequest(placementApproval);
            transferResponse = ncbsRequestService.transferSknRtgs(request);
        } else {
            return 0;
        }
        
        if (!"000".equals(transferResponse.getResponseCode())) {
            errorMessageDTOList.add(ErrorMessageUtil.getCreditTransferBiFastErrorMessage(
                    transferResponse.getResponseCode(), transferResponse.getResponseMessage()));
            return 0;
        }

        saveTransactionLog(placementApproval.getId(), transactionType, transferResponse.getResponseCode(), transferResponse.getResponseMessage());
        return 1;
    }

    private void saveTransactionLog(Long placementApprovalId, String transactionType, String responseCode, String responseMessage) {
        PlacementTransactionLog transactionLog = PlacementTransactionLog.builder()
                .placementApprovalId(placementApprovalId)
                .transactionType(transactionType)
                .responseCode(responseCode)
                .responseMessage(responseMessage)
                .createdDate(LocalDateTime.now())
                .build();
        placementTransactionLogRepository.save(transactionLog);
    }

    private PlacementApprovalResponse buildResponse(int totalSuccess, int totalFailed, List<ErrorMessageDTO> errorMessageDTOList) {
        return PlacementApprovalResponse.builder()
                .totalDataSuccess(totalSuccess)
                .totalDataFailed(totalFailed)
                .errorMessageDTOList(errorMessageDTOList)
                .build();
    }
}

```

# Process Internal Placement
```java
private int processInternalPlacement(PlacementApproval placementApproval, List<ErrorMessageDTO> errorMessageDTOList) throws JsonProcessingException {
    OverbookingCasaRequest request = createOverbookingCasaRequest(placementApproval);
    OverbookingCasaResponse response = ncbsRequestService.overbookingCasa(request);

    boolean isSuccess = "000".equals(response.getResponseCode());

    if (!isSuccess) {
        errorMessageDTOList.add(
                ErrorMessageUtil.getOverbookingCasaErrorMessage(
                        response.getResponseCode(),
                        response.getResponseMessage()
                )
        );
    }

    saveNCBSResponse(placementApproval, response, isSuccess);
    return isSuccess ? 1 : 0;
}

private void saveNCBSResponse(PlacementApproval placementApproval, OverbookingCasaResponse response, boolean isSuccess) throws JsonProcessingException {
    CreateNCBSResponse createNCBSResponse = CreateNCBSResponse.builder()
            .createdDate(LocalDateTime.now())
            .placementId(placementApproval.getId())
            .placementType(placementApproval.getPlacementType())
            .placementTransferType(placementApproval.getPlacementTransferType())
            .responseCode(response.getResponseCode())
            .responseMessage(response.getResponseMessage())
            .responseJson(objectMapper.writeValueAsString(response))
            .status(isSuccess ? "SUCCESS" : "FAILED")
            .build();

    NCBSResponse ncbsResponse = ncbsResponseService.saveToDatabase(createNCBSResponse);
    log.info("[NCBS Response] Success save with id: {}", ncbsResponse.getId());
}
```

# Save NCBS Response

```java
private void saveNCBSResponse(PlacementApproval placementApproval, Object response, boolean isSuccess) {
    try {
        String responseJson;
        try {
            responseJson = objectMapper.writeValueAsString(response);
        } catch (JsonProcessingException e) {
            log.error("Error serializing response to JSON: {}", e.getMessage(), e);
            responseJson = "{\"error\": \"Failed to serialize response\"}";
        }

        CreateNCBSResponse createNCBSResponse = CreateNCBSResponse.builder()
                .createdDate(LocalDateTime.now())
                .placementId(placementApproval.getId())
                .placementType(placementApproval.getPlacementType())
                .placementTransferType(placementApproval.getPlacementTransferType())
                .responseCode(extractResponseCode(response))  // Ambil response code dari object dinamis
                .responseMessage(extractResponseMessage(response)) // Ambil response message dari object dinamis
                .responseJson(responseJson)
                .status(isSuccess ? "SUCCESS" : "FAILED")
                .build();

        NCBSResponse ncbsResponse = ncbsResponseService.saveToDatabase(createNCBSResponse);
        log.info("[NCBS Response] Successfully saved with ID: {}", ncbsResponse.getId());
    } catch (Exception e) {
        log.error("Failed to save NCBS response for placementId {}: {}", placementApproval.getId(), e.getMessage(), e);
    }
}

```

```java
private String extractResponseCode(Object response) {
    try {
        if (response == null) return "UNKNOWN";

        if (response instanceof OverbookingCasaResponse) {
            return ((OverbookingCasaResponse) response).getResponseCode();
        } else if (response instanceof InquiryAccountResponse) {
            return ((InquiryAccountResponse) response).getResponseCode();
        }
        // Tambahkan tipe response lain jika diperlukan
    } catch (Exception e) {
        log.error("Failed to extract responseCode: {}", e.getMessage(), e);
    }
    return "UNKNOWN";
}

private String extractResponseMessage(Object response) {
    try {
        if (response == null) return "No Response Message";

        if (response instanceof OverbookingCasaResponse) {
            return ((OverbookingCasaResponse) response).getResponseMessage();
        } else if (response instanceof InquiryAccountResponse) {
            return ((InquiryAccountResponse) response).getResponseMessage();
        }
        // Tambahkan tipe response lain jika diperlukan
    } catch (Exception e) {
        log.error("Failed to extract responseMessage: {}", e.getMessage(), e);
    }
    return "No Response Message";
}
```

```java
private boolean validateInquiryAccount(PlacementApproval placementApproval, List<ErrorMessageDTO> errorMessageDTOList) {
    try {
        InquiryAccountRequest request = ncbsRequestService.createInquiryAccountRequest(placementApproval);
        saveNCBSRequest(placementApproval, request); // Save request to NCBS Request table

        InquiryAccountResponse response = ncbsRequestService.inquiryAccount(request);
        boolean isSuccess = "000".equals(response.getResponseCode());

        if (!isSuccess) {
            errorMessageDTOList.add(
                    ErrorMessageUtil.getInquiryAccountErrorMessage(
                            response.getResponseCode(),
                            response.getResponseMessage()
                    )
            );
        }

        saveNCBSResponse(placementApproval, response, isSuccess); // Save response to NCBS Response table
        return isSuccess;

    } catch (Exception e) {
        log.error("Error during validateInquiryAccount for placementId {}: {}", placementApproval.getId(), e.getMessage(), e);
        return false;
    }
}

```

# Simpan Request ke NCBS Request table
```java
private void saveNCBSRequest(PlacementApproval placementApproval, InquiryAccountRequest request) {
    try {
        String requestJson = convertObjectToJson(request);
        CreateNCBSRequest createNCBSRequest = CreateNCBSRequest.builder()
                .createdDate(LocalDateTime.now())
                .placementId(placementApproval.getId())
                .placementType(placementApproval.getPlacementType())
                .placementProcessType(placementApproval.getPlacementProcessType())
                .placementTransferType(placementApproval.getPlacementTransferType())
                .requestJson(requestJson)
                .build();

        NCBSRequest ncbsRequest = ncbsRequestService.saveToDatabase(createNCBSRequest);
        log.info("[NCBS Request] Successfully saved with ID: {}", ncbsRequest.getId());
    } catch (Exception e) {
        log.error("Failed to save NCBS Request for placementId {}: {}", placementApproval.getId(), e.getMessage(), e);
    }
}

```

# Simpan Response ke NCBS Response table
```java
private void saveNCBSResponse(PlacementApproval placementApproval, InquiryAccountResponse response, boolean isSuccess) {
    try {
        String responseJson = convertObjectToJson(response);
        CreateNCBSResponse createNCBSResponse = CreateNCBSResponse.builder()
                .createdDate(LocalDateTime.now())
                .placementId(placementApproval.getId())
                .placementType(placementApproval.getPlacementType())
                .placementProcessType(placementApproval.getPlacementProcessType())
                .placementTransferType(placementApproval.getPlacementTransferType())
                .responseCode(response.getResponseCode())
                .responseMessage(response.getResponseMessage())
                .responseJson(responseJson)
                .status(isSuccess ? "SUCCESS" : "FAILED")
                .build();

        NCBSResponse ncbsResponse = ncbsResponseService.saveToDatabase(createNCBSResponse);
        log.info("[NCBS Response] Successfully saved with ID: {}", ncbsResponse.getId());
    } catch (Exception e) {
        log.error("Failed to save NCBS Response for placementId {}: {}", placementApproval.getId(), e.getMessage(), e);
    }
}

```

# Process Transfer External

```java
private int processExternalTransfer(PlacementApproval placementApproval, List<ErrorMessageDTO> errorMessageDTOList) {
    String placementTransferType = placementApproval.getPlacementTransferType();
    TransferResponse transferResponse = new TransferResponse();

    try {
        if ("BI-FAST".equalsIgnoreCase(placementTransferType)) {
            CreditTransferRequest request = createCreditTransferBiFastRequest(placementApproval);
            CreditTransferResponse response = ncbsRequestService.creditTransfer(request);
            transferResponse = mapToTransferResponse(response);
        } else if ("SKN".equalsIgnoreCase(placementTransferType) || "RTGS".equalsIgnoreCase(placementTransferType)) {
            TransferSknRtgsRequest request = createTransferSknRtgsRequest(placementApproval);
            TransferSknRtgsResponse response = ncbsRequestService.transferSknRtgs(request);
            transferResponse = mapToTransferResponse(response);
        } else {
            log.warn("Unsupported placementTransferType: {}", placementTransferType);
            return 0;
        }

        // Save response to database
        saveNCBSResponse(placementApproval, transferResponse, "000".equals(transferResponse.getResponseCode()));

        if (!"000".equals(transferResponse.getResponseCode())) {
            errorMessageDTOList.add(
                    ErrorMessageUtil.getCreditTransferBiFastErrorMessage(
                            transferResponse.getResponseCode(),
                            transferResponse.getResponseMessage()
                    )
            );
            return 0;
        }

        return 1;
    } catch (Exception e) {
        log.error("Error processing external transfer for placementId {}: {}", placementApproval.getId(), e.getMessage(), e);
        errorMessageDTOList.add(new ErrorMessageDTO("SYSTEM_ERROR", "Failed to process external transfer"));
        return 0;
    }
}

/**
 * Helper method to convert different response types to TransferResponse
 */
private TransferResponse mapToTransferResponse(Object response) throws JsonProcessingException {
    TransferResponse transferResponse = new TransferResponse();
    if (response instanceof CreditTransferResponse) {
        CreditTransferResponse creditResponse = (CreditTransferResponse) response;
        transferResponse.setResponseCode(creditResponse.getResponseCode());
        transferResponse.setResponseMessage(creditResponse.getResponseMessage());
    } else if (response instanceof TransferSknRtgsResponse) {
        TransferSknRtgsResponse sknRtgsResponse = (TransferSknRtgsResponse) response;
        transferResponse.setResponseCode(sknRtgsResponse.getResponseCode());
        transferResponse.setResponseMessage(sknRtgsResponse.getResponseMessage());
    }
    transferResponse.setResponseJson(objectMapper.writeValueAsString(response));
    return transferResponse;
}

/**
 * Save NCBS response to database with success or failure status
 */
private void saveNCBSResponse(PlacementApproval placementApproval, TransferResponse transferResponse, boolean isSuccess) {
    try {
        CreateNCBSResponse createNCBSResponse = CreateNCBSResponse.builder()
                .placementId(placementApproval.getId())
                .placementType(placementApproval.getPlacementType())
                .placementProcessType(placementApproval.getPlacementProcessType())
                .placementTransferType(placementApproval.getPlacementTransferType())
                .responseCode(transferResponse.getResponseCode())
                .responseMessage(transferResponse.getResponseMessage())
                .responseJson(transferResponse.getResponseJson())
                .status(isSuccess ? "SUCCESS" : "FAILED")
                .build();
        ncbsResponseService.saveToDatabase(createNCBSResponse);
    } catch (Exception e) {
        log.error("Failed to save NCBS Response for placementId {}: {}", placementApproval.getId(), e.getMessage(), e);
    }
}

```

# Credit Transfer Request

```json
{
  "trxType": "CREDIT_TRANSFER",
  "category": "01",
  "sttlmAmt": "1234.56",
  "sttlmCcy": "IDR",
  "sttlmDate": "2021-03-19",
  "feeAmt": "1000.00",
  "chargeBearerCode": "DEBT",
  "senderAcctNo": "1234566789",
  "senderAcctType": "SVGS",
  "senderBic": "INDOIDJA",
  "benefBic": "CENAIDJA",
  "benefName": "JOHN SMITH",
  "benefId": "0102030405060708",
  "benefAcctNo": "987654321",
  "benefAcctType": "SVGS",
  "proxyType": "02",
  "proxyValue": "john.smith@example.com",
  "description": "Payment for housing",
  "benefType": "01",
  "benefResidentStatus": "01",
  "benefCityCode": "0300",
  "purposeTransaction": "01",
  "cardNo": "5577917000001211"
}
```

# Overbooking Casa Request

```json
{
  "header": [
    {
      "codOrgBrn": "9999"
    }
  ],
  "body": {
    "acctIdFrom": {
      "aTMCardNo": "",
      "acctIdF": "003558780833",
      "acctTypeF": "26",
      "costCtrF": ""
    },
    "acctIdTo": {
      "acctIdT": "003533543900",
      "acctTypeT": "26",
      "costCtrT": ""
    },
    "xferInfo": {
      "xferAmt": "10000",
      "xferdesc1": "7915000009394585 PEMBAYARAN UNTUK AKULAKU FINANCE",
      "xferdesc2": "AKULAKU FINANCE"
    }
  }
}
```

# Transfer SKN RTGS Request
```json
{
  "xferInfoFrom": {
    "acctId": "903600415859",
    "acctType": "26",
    "acctCur": "360",
    "bdiAcctStatus": "Y",
    "bdiAcctCitizen": "Y"
  },
  "bdiXferXFOff": {
    "bdiXferAmtFrm": "1000000000",
    "bdiXferAmtFrmLCE": "1000000000",
    "bdiXferAmtTo": "1000000000",
    "bdiXferAmtToLCE": "1000000000",
    "bdiXferType": "4",
    "bdiXferCurCode": "0",
    "bdiXRateAmt": "100",
    "bdiStdRateAmt": "100",
    "bdiXReffNumber": "",
    "bdiXferBeneficiery": {
      "bdiBenfID": " ",
      "bdiBenfAcct": "29895023100",
      "bdiBenfName": "ASTRI BUDIARTI",
      "bdiBenfAddress": " ",
      "bdiBenStatus": "Y",
      "bdiBenCitizen": "Y",
      "bankInfo": {
        "biCode": "1100019",
        "cocCode": "901",
        "name": "Jakarta"
      }
    },
    "bdiXferCostCtr": "7101",
    "bdiFeeAmt": "3500000",
    "bdiFeeAmtLCE": "3500000",
    "bdiFeeProcIr": "S",
    "bdiXferMemo": {
      "bdiFrMemo1": "Internet Trf dari AUDI MAULANA",
      "bdiFrMemo2": "ke ASTRI BUDIARTI 11",
      "bdiToMemo1": "ke ASTRI BUDIARTI 11",
      "bdiToMemo2": "Internet Trf dari AUDI MAULANA"
    },
    "transInfo": {
      "trn": "IFT00000",
      "fee": " BEN"
    },
    "lldInfo": "ID015"
  }
}
```

# Move Inquiry Account process to Placement External

```java
public PlacementApprovalResponse approve(Long placementDepositApprovalId, String approveId, String approveIPAddress) {
    log.info("Start approve placement approval with id: {}, approveId: {}, and approveIPAddress: {}", placementDepositApprovalId, approveId, approveIPAddress);

    int totalSuccess = 0;
    int totalFailed = 0;
    List<ErrorMessageDTO> errorMessageDTOList = new ArrayList<>();
    PlacementApproval placementApproval;

    // **Step 1: Retrieve Placement Approval**
    try {
        placementApproval = placementApprovalRepository.findById(placementDepositApprovalId)
                .orElseThrow(() -> new DataNotFoundException("Placement Approval not found with id: " + placementDepositApprovalId));

        placementApproval.setApprovalStatus(ApprovalStatus.Approved);
        placementApproval.setApproverId(approveId);
        placementApproval.setApproveIPAddress(approveIPAddress);
        placementApproval.setApproveDate(LocalDateTime.now());

        placementApproval = placementApprovalRepository.save(placementApproval);
        log.info("Save approve placement approval: {}", placementApproval);
    } catch (Exception e) {
        log.error("Error retrieving or updating placement approval: {}", e.getMessage(), e);
        errorMessageDTOList.add(new ErrorMessageDTO("RETRIEVE_ERROR", Collections.singletonList("Error retrieving placement approval: " + e.getMessage())));
        return buildPlacementApprovalResponse(totalSuccess, ++totalFailed, errorMessageDTOList);
    }

    // **Step 2: Process Placement**
    try {
        if (PLACEMENT_TYPE_INTERNAL.equalsIgnoreCase(placementApproval.getPlacementType())) {
            totalSuccess += processInternalPlacement(placementApproval, errorMessageDTOList);
        } else if (PLACEMENT_TYPE_EXTERNAL.equalsIgnoreCase(placementApproval.getPlacementType())) {
            // **Inquiry Account dipindahkan ke dalam blok ini**
            InquiryAccountResponse inquiryResponse = validateInquiryAccount(placementApproval, errorMessageDTOList);
            if (inquiryResponse == null) {
                log.error("Inquiry Account failed for placement ID: {}", placementApproval.getId());
                return buildPlacementApprovalResponse(totalSuccess, ++totalFailed, errorMessageDTOList);
            }

            log.info("[Inquiry Response] Data: {}", inquiryResponse);

            totalSuccess += processExternalTransfer(inquiryResponse, placementApproval, errorMessageDTOList);
        }
    } catch (Exception e) {
        log.error("Error processing placement transfer for placementId {}: {}", placementApproval.getId(), e.getMessage(), e);
        errorMessageDTOList.add(new ErrorMessageDTO(PROCESS_PLACEMENT_ERROR, Collections.singletonList("Error occurred while processing transfer: " + e.getMessage())));
        totalFailed++;
    }

    return buildPlacementApprovalResponse(totalSuccess, totalFailed, errorMessageDTOList);
}

```

# Approval Request
```json
{
  "approverId": "",
  "placementApprovalIds": [1, 2, 3, 4, 5]
}
```

# Inquiry Account

```java
private InquiryAccountResponse validateInquiryAccount(
        InquiryAccountRequest inquiryAccountRequest, 
        PlacementApproval placementApproval, 
        List<ErrorMessageDTO> errorMessageDTOList) {

    log.info("Start validateInquiryAccount for placementId: {}", placementApproval.getId());

    InquiryAccountResponse inquiryResponse = new InquiryAccountResponse();
    
    try {
        // **1: Save request object to the table NCBS Request**
        saveNCBSRequest(placementApproval, convertObjectToJson(inquiryAccountRequest));
        log.info("Saved NCBS request for placementApprovalId: {}", placementApproval.getId());

        // **2: Send request to NCBS API**
        inquiryResponse = ncbsRequestService.inquiryAccount(inquiryAccountRequest);
        log.debug("Received InquiryAccountResponse: {}", inquiryResponse);

        // **3: Check response**
        if (!RESPONSE_CODE_SUCCESS.equals(inquiryResponse.getResponseCode())) {
            log.warn("Inquiry account failed for placementApprovalId: {} with responseCode: {} and message: {}",
                    placementApproval.getId(), inquiryResponse.getResponseCode(), inquiryResponse.getResponseMessage());

            errorMessageDTOList.add(ErrorMessageUtil.getInquiryAccountErrorMessage(
                    inquiryResponse.getResponseCode(), inquiryResponse.getResponseMessage()));
        }

        // **4: Save response object to the table NCBS Response**
        String responseJson = convertObjectToJson(inquiryResponse);
        saveNCBSResponse(placementApproval, inquiryResponse.getResponseCode(), 
                         inquiryResponse.getResponseMessage(), responseJson, true);
        log.info("Saved NCBS response for placementApprovalId: {}", placementApproval.getId());

    } catch (HttpClientErrorException e) {
        log.error("HTTP request error during validateInquiryAccount for placementApprovalId {}: {}", 
                  placementApproval.getId(), e.getMessage(), e);
        inquiryResponse.setResponseCode(INQUIRY_ERROR);
        inquiryResponse.setResponseMessage("HTTP error: " + e.getMessage());
        errorMessageDTOList.add(new ErrorMessageDTO(INQUIRY_ERROR, 
                Collections.singletonList("HTTP error: " + e.getMessage())));
    } catch (Exception e) {
        log.error("Unexpected error during validateInquiryAccount for placementId {}: {}", 
                  placementApproval.getId(), e.getMessage(), e);
        inquiryResponse.setResponseCode(INQUIRY_ERROR);
        inquiryResponse.setResponseMessage("Unexpected error: " + e.getMessage());
        errorMessageDTOList.add(new ErrorMessageDTO(INQUIRY_ERROR, 
                Collections.singletonList("Unexpected error: " + e.getMessage())));
    }

    return inquiryResponse;
}

```

            try {

//                placementApproval.setApprovalStatus(ApprovalStatus.Approved);
//                placementApproval.setApproverId(approveId);
//                placementApproval.setApproveIPAddress(approveIPAddress);
//                placementApproval.setApproveDate(LocalDateTime.now());
//
//                placementApproval = placementApprovalRepository.save(placementApproval);
//                log.info("Save approve placement approval: {}", placementApproval);
//            } catch (Exception e) {
//                log.error("Error retrieving or updating placement approval: {}", e.getMessage(), e);
//                errorMessageDTOList.add(new ErrorMessageDTO(RETRIEVE_PLACEMENT_ERROR, Collections.singletonList("Error retrieving placement approval: " + e.getMessage())));
//                return buildPlacementApprovalResponse(totalSuccess, ++totalFailed, errorMessageDTOList);
//            }


# External (need inquiry account)
```java
private TransferResponse processExternalTransfer(
        InquiryAccountDataDTO inquiryAccountDataDTO,
        PlacementApproval placementApproval,
        List<ErrorMessageDTO> errorMessageDTOList) {

    log.info("[External Placement] Inquiry Account: {}, Placement: {}, Error Messages: {}", 
            inquiryAccountDataDTO, placementApproval, errorMessageDTOList);

    String placementTransferType = placementApproval.getPlacementTransferType();
    TransferResponse transferResponse = new TransferResponse();

    try {
        if (BI_FAST.equalsIgnoreCase(placementTransferType)) {
            // **1: Create credit transfer Bi-Fast request**
            CreditTransferRequest request = createCreditTransferBiFastRequest(inquiryAccountDataDTO, placementApproval);
            // **2: Save request object to the table NCBS Request**
            saveNCBSRequest(placementApproval, convertObjectToJson(request));
            // **3: Send request to NCBS API**
            CreditTransferResponse response = ncbsRequestService.creditTransfer(placementApproval, request);
            // **4: Get response**
            transferResponse.setResponseCode(response.getResponseCode());
            transferResponse.setResponseMessage(response.getResponseMessage());
            transferResponse.setTransferType(BI_FAST);
            transferResponse.setResponseJson(objectMapper.writeValueAsString(response));

        } else if (SKN.equalsIgnoreCase(placementTransferType) || RTGS.equalsIgnoreCase(placementTransferType)) {
            // **1: Create transfer SKN RTGS request**
            TransferSknRtgsRequest request = createTransferSknRtgsRequest(inquiryAccountDataDTO, placementApproval);
            // **2: Save request object to the table NCBS Request**
            saveNCBSRequest(placementApproval, convertObjectToJson(request));
            // **3: Send request to NCBS API**
            TransferSknRtgsResponse response = ncbsRequestService.transferSknRtgs(placementApproval, request);
            // **4: Get response**
            transferResponse.setResponseCode(response.getResponseCode());
            transferResponse.setResponseMessage(response.getResponseMessage());
            transferResponse.setTransferType(SKN + "-" + RTGS);
            transferResponse.setResponseJson(objectMapper.writeValueAsString(response));

        } else {
            log.warn("Unsupported placementTransferType: {}", placementTransferType);
            transferResponse.setResponseCode("INVALID_TYPE");
            transferResponse.setResponseMessage("Unsupported transfer type: " + placementTransferType);
        }

        // **5: Check response code**
        boolean isSuccess = RESPONSE_CODE_SUCCESS.equals(transferResponse.getResponseCode());

        if (!isSuccess) {
            // **6: Create response detail**
            ErrorMessageDTO errorMessageDTO;
            if (BI_FAST.equalsIgnoreCase(transferResponse.getTransferType())) {
                errorMessageDTO = ErrorMessageUtil.getCreditTransferBiFastErrorMessage(
                        transferResponse.getResponseCode(),
                        transferResponse.getResponseMessage()
                );
            } else {
                errorMessageDTO = ErrorMessageUtil.getTransferSknRtgsErrorMessage(
                        transferResponse.getResponseCode(),
                        transferResponse.getResponseMessage()
                );
            }
            errorMessageDTOList.add(errorMessageDTO);
        }

        // **7: Save response to the table NCBS Response**
        saveNCBSResponse(placementApproval, transferResponse.getResponseCode(), 
                         transferResponse.getResponseMessage(), transferResponse.getResponseJson(), isSuccess);

    } catch (Exception e) {
        log.error("Error processing external transfer for placementId {}: {}", 
                  placementApproval.getId(), e.getMessage(), e);
        transferResponse.setResponseCode("SYSTEM_ERROR");
        transferResponse.setResponseMessage("Failed to process external transfer: " + e.getMessage());
        errorMessageDTOList.add(
                new ErrorMessageDTO("SYSTEM_ERROR", Collections.singletonList("Failed to process external transfer: " + e.getMessage()))
        );
    }

    return transferResponse;
}

```

# Approval Service

```java
public synchronized PlacementApprovalResponse approve(
        List<Long> placementApprovalIds, String approveId, String approveIPAddress) {

    log.info("Start approving placement approvals: {}, Approver ID: {}, IP: {}", 
            placementApprovalIds, approveId, approveIPAddress);

    int totalSuccess = 0;
    int totalFailed = 0;
    List<ErrorMessageDTO> errorMessageDTOList = new ArrayList<>();

    for (Long placementApprovalId : placementApprovalIds) {
        try {
            PlacementApproval placementApproval = fetchPlacementApproval(placementApprovalId);
            
            if (PLACEMENT_TYPE_INTERNAL.equalsIgnoreCase(placementApproval.getPlacementType())) {
                totalSuccess += processInternalPlacement(placementApproval, errorMessageDTOList);
            } else if (PLACEMENT_TYPE_EXTERNAL.equalsIgnoreCase(placementApproval.getPlacementType())) {
                totalSuccess += processExternalPlacement(placementApproval, approveId, approveIPAddress, errorMessageDTOList);
            }

        } catch (Exception e) {
            log.error("Error processing placement transfer for ID {}: {}", placementApprovalId, e.getMessage(), e);
            errorMessageDTOList.add(new ErrorMessageDTO(PROCESS_PLACEMENT_ERROR, 
                    Collections.singletonList("Error processing transfer: " + e.getMessage())));
            totalFailed++;
        }
    }

    return buildPlacementApprovalResponse(totalSuccess, totalFailed, errorMessageDTOList);
}

private PlacementApproval fetchPlacementApproval(Long placementApprovalId) {
    return placementApprovalRepository.findById(placementApprovalId)
            .orElseThrow(() -> new DataNotFoundException("Placement Approval not found with ID: " + placementApprovalId));
}

private int processExternalPlacement(
        PlacementApproval placementApproval, String approveId, String approveIPAddress, 
        List<ErrorMessageDTO> errorMessageDTOList) {

    InquiryAccountRequest inquiryAccountRequest = createInquiryAccountRequest(placementApproval);
    log.info("[Inquiry Account] Request created: {}", inquiryAccountRequest);

    InquiryAccountResponse inquiryAccountResponse = validateInquiryAccount(inquiryAccountRequest, placementApproval, errorMessageDTOList);
    
    if (!"200R000000".equals(inquiryAccountResponse.getResponseCode())) {
        return handleFailedApproval(placementApproval, approveId, approveIPAddress, 
                inquiryAccountResponse.getResponseCode(), inquiryAccountResponse.getResponseMessage());
    }

    log.info("[Inquiry Account] Response data: {}", inquiryAccountResponse);
    InquiryAccountDataDTO inquiryAccountDTO = inquiryAccountResponse.getData();

    TransferResponse transferResponse = processExternalTransfer(inquiryAccountDTO, placementApproval, errorMessageDTOList);

    if (!"200R000000".equals(transferResponse.getResponseCode())) {
        throw new TransferPlacementException(transferResponse.getResponseCode(), transferResponse.getResponseMessage());
    }

    return handleSuccessfulApproval(placementApproval, approveId, approveIPAddress, 
            transferResponse.getResponseCode(), transferResponse.getResponseMessage());
}

private int handleFailedApproval(PlacementApproval placementApproval, String approveId, String approveIPAddress, 
                                 String responseCode, String responseMessage) {

    updatePlacementApproval(placementApproval, approveId, approveIPAddress, ApprovalStatus.Pending, 
            responseCode, responseMessage, NCBS_STATUS_FAILED);
    
    log.error("[Inquiry Account] Failed for placement approval ID: {}", placementApproval.getId());
    return 0; // Failed processing, return 0 success count
}

private int handleSuccessfulApproval(PlacementApproval placementApproval, String approveId, String approveIPAddress, 
                                     String responseCode, String responseMessage) {

    updatePlacementApproval(placementApproval, approveId, approveIPAddress, ApprovalStatus.Approved, 
            responseCode, responseMessage, NCBS_STATUS_SUCCESS);
    
    return 1; // Successful processing, return 1 success count
}

private void updatePlacementApproval(PlacementApproval placementApproval, String approveId, String approveIPAddress, 
                                     ApprovalStatus status, String responseCode, String responseMessage, String ncbsStatus) {
    
    placementApproval.setApprovalStatus(status);
    placementApproval.setApproverId(approveId);
    placementApproval.setApproveIPAddress(approveIPAddress);
    placementApproval.setApproveDate(LocalDateTime.now());
    placementApproval.setNcbsStatus(ncbsStatus);
    placementApproval.setNcbsResponseCode(responseCode);
    placementApproval.setNcbsResponseMessage(responseMessage);

    placementApprovalRepository.save(placementApproval);
}

```

# Overbooking Casa jika Response Success

- update data PlacementApproval
- update NCBS Status = Success
- update approvalStatus = Approved

# Overbooking Casa jika Response Failed

- update data PlacementApproval
- update NCBS Status = Failed
- update approvalStatus = Approved

# Refactor Placement Approval Service

```java
@Override
public synchronized PlacementApprovalResponse approve(List<Long> placementApprovalIds, String approveId, String approveIPAddress, String statusRun) {
    log.info("Start approve placement approval with id: {}, approveId: {}, and approveIPAddress: {}", placementApprovalIds, approveId, approveIPAddress);
    
    int totalSuccess = 0;
    int totalFailed = 0;
    List<ErrorMessageDTO> errorMessageDTOList = new ArrayList<>();

    for (Long placementApprovalId : placementApprovalIds) {
        try {
            PlacementApproval placementApproval = placementApprovalRepository.findById(placementApprovalId)
                    .orElseThrow(() -> new DataNotFoundException("Placement Approval not found with id: " + placementApprovalId));
            
            if (RERUN.equalsIgnoreCase(statusRun)) {
                placementApproval.setReferenceId(GenerateUniqueKeyUtil.generateReferenceId());
            }
            
            boolean isSuccess = processApproval(placementApproval, approveId, approveIPAddress, errorMessageDTOList);
            if (isSuccess) {
                totalSuccess++;
            } else {
                totalFailed++;
            }
        } catch (Exception e) {
            handleApprovalError(placementApprovalId, e, errorMessageDTOList);
            totalFailed++;
        }
    }
    return buildPlacementApprovalResponse(totalSuccess, totalFailed, errorMessageDTOList);
}

private boolean processApproval(PlacementApproval placementApproval, String approveId, String approveIPAddress, List<ErrorMessageDTO> errorMessageDTOList) {
    if (PLACEMENT_TYPE_INTERNAL.equalsIgnoreCase(placementApproval.getPlacementType())) {
        return processInternalTransfer(placementApproval, approveId, approveIPAddress, errorMessageDTOList);
    } else if (PLACEMENT_TYPE_EXTERNAL.equalsIgnoreCase(placementApproval.getPlacementType())) {
        return processExternalTransfer(placementApproval, approveId, approveIPAddress, errorMessageDTOList);
    }
    log.warn("Unsupported placement type: {}", placementApproval.getPlacementType());
    return false;
}

private boolean processInternalTransfer(PlacementApproval placementApproval, String approveId, String approveIPAddress, List<ErrorMessageDTO> errorMessageDTOList) {
    OverbookingCasaRequest request = createOverbookingCasaRequest(placementApproval);
    saveNCBSRequest(placementApproval, OVERBOOKING_CASA, convertObjectToJson(request));

    OverbookingCasaResponse response = ncbsRequestService.overbookingCasa(placementApproval.getReferenceId(), request);
    saveNCBSResponse(placementApproval, response.getResponseCode(), response.getResponseMessage(), response.getSubStatusProvider(), convertObjectToJson(response));

    return finalizeApproval(placementApproval, approveId, approveIPAddress, response.getResponseCode(), response.getResponseMessage(), errorMessageDTOList, "Overbooking Casa");
}

private boolean processExternalTransfer(PlacementApproval placementApproval, String approveId, String approveIPAddress, List<ErrorMessageDTO> errorMessageDTOList) {
    String placementTransferType = placementApproval.getPlacementTransferType();
    if (BI_FAST.equalsIgnoreCase(placementTransferType)) {
        return processBiFastTransfer(placementApproval, approveId, approveIPAddress, errorMessageDTOList);
    } else if (SKN.equalsIgnoreCase(placementTransferType) || RTGS.equalsIgnoreCase(placementTransferType)) {
        return processSknRtgsTransfer(placementApproval, approveId, approveIPAddress, errorMessageDTOList);
    }
    log.warn("Unsupported placement transfer type: {}", placementTransferType);
    return false;
}

private boolean processBiFastTransfer(PlacementApproval placementApproval, String approveId, String approveIPAddress, List<ErrorMessageDTO> errorMessageDTOList) {
    InquiryAccountRequest inquiryRequest = createInquiryAccountRequest(placementApproval);
    saveNCBSRequest(placementApproval, INQUIRY_ACCOUNT, convertObjectToJson(inquiryRequest));
    
    InquiryAccountResponse inquiryResponse = ncbsRequestService.inquiryAccount(inquiryRequest);
    saveNCBSResponse(placementApproval, inquiryResponse.getResponseCode(), inquiryResponse.getResponseMessage(), inquiryResponse.getSubStatusProvider(), convertObjectToJson(inquiryResponse));

    if (!API_RESPONSE_CODE_SUCCESS.equals(inquiryResponse.getResponseCode())) {
        errorMessageDTOList.add(new ErrorMessageDTO(placementApproval.getSiReferenceID(), Collections.singletonList("Inquiry Account failed: " + inquiryResponse.getResponseMessage())));
        return false;
    }
    
    CreditTransferRequest creditRequest = createCreditTransferBiFastRequest(inquiryResponse.getData(), placementApproval);
    saveNCBSRequest(placementApproval, CREDIT_TRANSFER, convertObjectToJson(creditRequest));
    
    CreditTransferResponse creditResponse = ncbsRequestService.creditTransfer(placementApproval.getReferenceId(), creditRequest);
    saveNCBSResponse(placementApproval, creditResponse.getResponseCode(), creditResponse.getResponseMessage(), creditResponse.getSubStatusProvider(), convertObjectToJson(creditResponse));
    
    return finalizeApproval(placementApproval, approveId, approveIPAddress, creditResponse.getResponseCode(), creditResponse.getResponseMessage(), errorMessageDTOList, "Credit Transfer");
}

private boolean processSknRtgsTransfer(PlacementApproval placementApproval, String approveId, String approveIPAddress, List<ErrorMessageDTO> errorMessageDTOList) {
    TransferSknRtgsRequest request = createTransferSknRtgsRequest(placementApproval);
    saveNCBSRequest(placementApproval, TRANSFER_SKN_RTGS, convertObjectToJson(request));
    
    TransferSknRtgsResponse response = ncbsRequestService.transferSknRtgs(placementApproval.getReferenceId(), request);
    saveNCBSResponse(placementApproval, response.getResponseCode(), response.getResponseMessage(), response.getSubStatusProvider(), convertObjectToJson(response));
    
    return finalizeApproval(placementApproval, approveId, approveIPAddress, response.getResponseCode(), response.getResponseMessage(), errorMessageDTOList, "Transfer SKN RTGS");
}

private boolean finalizeApproval(PlacementApproval placementApproval, String approveId, String approveIPAddress, String responseCode, String responseMessage, List<ErrorMessageDTO> errorMessageDTOList, String processName) {
    boolean isSuccess = API_RESPONSE_CODE_SUCCESS.equals(responseCode);
    placementApproval.setApprovalStatus(ApprovalStatus.Approved);
    placementApproval.setApproverId(approveId);
    placementApproval.setApproveIPAddress(approveIPAddress);
    placementApproval.setApproveDate(LocalDateTime.now());
    placementApproval.setNcbsStatus(isSuccess ? NCBS_STATUS_SUCCESS : NCBS_STATUS_FAILED);
    placementApproval.setNcbsResponseCode(responseCode);
    placementApproval.setNcbsResponseMessage(responseMessage);
    placementApprovalRepository.save(placementApproval);
    
    if (!isSuccess) {
        errorMessageDTOList.add(new ErrorMessageDTO(placementApproval.getSiReferenceID(), Collections.singletonList(processName + " failed: " + responseMessage)));
    }
    return isSuccess;
}

private void handleApprovalError(Long placementApprovalId, Exception e, List<ErrorMessageDTO> errorMessageDTOList) {
    log.error("Error processing placement transfer for placementId {}: {}", placementApprovalId, e.getMessage(), e);
    errorMessageDTOList.add(new ErrorMessageDTO(placementApprovalId.toString(), Collections.singletonList("Error occurred while processing transfer: " + e.getMessage())));
}

```

# Placement Approval Service Old

```java
    @Override
    public synchronized PlacementApprovalResponse approve(List<Long> placementApprovalIds, String approveId, String approveIPAddress, String statusRun) {
        log.info("Start approve placement approval with id: {}, approveId: {}, and approveIPAddress: {}", placementApprovalIds, approveId, approveIPAddress);
        int totalSuccess = 0;
        int totalFailed = 0;
        List<ErrorMessageDTO> errorMessageDTOList = new ArrayList<>();

        for (Long placementApprovalId : placementApprovalIds) {
            try {
                // **Step 1: Retrieve Placement Approval**
                PlacementApproval placementApproval = placementApprovalRepository.findById(placementApprovalId).orElseThrow(() -> new DataNotFoundException("Placement Approval not found with id: " + placementApprovalId));

                // Check re-run or not
                if (RERUN.equalsIgnoreCase(statusRun)) {
                    placementApproval.setReferenceId(GenerateUniqueKeyUtil.generateReferenceId());
                }

                // **Step 2: Process Transfer**
                if (PLACEMENT_TYPE_INTERNAL.equalsIgnoreCase(placementApproval.getPlacementType())) {
                    OverbookingCasaRequest request = createOverbookingCasaRequest(placementApproval);
                    saveNCBSRequest(placementApproval, OVERBOOKING_CASA, convertObjectToJson(request));

                    OverbookingCasaResponse overbookingCasaResponse = ncbsRequestService.overbookingCasa(placementApproval.getReferenceId(), request);
                    saveNCBSResponse(placementApproval, overbookingCasaResponse.getResponseCode(), overbookingCasaResponse.getResponseMessage(), overbookingCasaResponse.getSubStatusProvider(), convertObjectToJson(overbookingCasaResponse));

                    boolean isSuccessOverbookingCasa = API_RESPONSE_CODE_SUCCESS.equals(overbookingCasaResponse.getResponseCode());

                    placementApproval.setApprovalStatus(ApprovalStatus.Approved);
                    placementApproval.setApproverId(approveId);
                    placementApproval.setApproveIPAddress(approveIPAddress);
                    placementApproval.setApproveDate(LocalDateTime.now());
                    placementApproval.setNcbsStatus(isSuccessOverbookingCasa ? NCBS_STATUS_SUCCESS : NCBS_STATUS_FAILED);
                    placementApproval.setNcbsResponseCode(overbookingCasaResponse.getResponseCode());
                    placementApproval.setNcbsResponseMessage(overbookingCasaResponse.getResponseMessage());
                    placementApprovalRepository.save(placementApproval);

                    if (isSuccessOverbookingCasa) {
                        totalSuccess++;
                    } else {
                        errorMessageDTOList.add(new ErrorMessageDTO(placementApproval.getSiReferenceID(), Collections.singletonList("Overbooking Casa is failed: " + overbookingCasaResponse.getResponseMessage())));
                        totalFailed++;
                    }

                } else if (PLACEMENT_TYPE_EXTERNAL.equalsIgnoreCase(placementApproval.getPlacementType())) {
                    String placementTransferType = placementApproval.getPlacementTransferType();

                    if (BI_FAST.equalsIgnoreCase(placementTransferType)) {
                        InquiryAccountRequest inquiryAccountRequest = createInquiryAccountRequest(placementApproval);
                        saveNCBSRequest(placementApproval, INQUIRY_ACCOUNT, convertObjectToJson(inquiryAccountRequest));

                        InquiryAccountResponse inquiryAccountResponse = ncbsRequestService.inquiryAccount(inquiryAccountRequest);
                        saveNCBSResponse(placementApproval, inquiryAccountResponse.getResponseCode(), inquiryAccountResponse.getResponseMessage(), inquiryAccountResponse.getSubStatusProvider(), convertObjectToJson(inquiryAccountResponse));

                        boolean isSuccessInquiry = API_RESPONSE_CODE_SUCCESS.equals(inquiryAccountResponse.getResponseCode());
                        placementApproval.setApprovalStatus(ApprovalStatus.Approved);
                        placementApproval.setApproverId(approveId);
                        placementApproval.setApproveIPAddress(approveIPAddress);
                        placementApproval.setApproveDate(LocalDateTime.now());
                        placementApproval.setNcbsStatus(isSuccessInquiry ? NCBS_STATUS_SUCCESS : NCBS_STATUS_FAILED);
                        placementApproval.setNcbsResponseCode(inquiryAccountResponse.getResponseCode());
                        placementApproval.setNcbsResponseMessage(inquiryAccountResponse.getResponseMessage());
                        placementApprovalRepository.save(placementApproval);

                        if (isSuccessInquiry) {
                            String referenceId = placementApproval.getReferenceId();
                            InquiryAccountDataDTO inquiryAccountDataDTO = inquiryAccountResponse.getData();

                            CreditTransferRequest creditTransferRequest = createCreditTransferBiFastRequest(inquiryAccountDataDTO, placementApproval);
                            saveNCBSRequest(placementApproval, CREDIT_TRANSFER, convertObjectToJson(creditTransferRequest));

                            CreditTransferResponse creditTransferResponse = ncbsRequestService.creditTransfer(referenceId, creditTransferRequest);
                            saveNCBSResponse(placementApproval, creditTransferResponse.getResponseCode(), creditTransferResponse.getResponseMessage(), creditTransferResponse.getSubStatusProvider(), convertObjectToJson(creditTransferResponse));

                            boolean isSuccessCreditTransfer = API_RESPONSE_CODE_SUCCESS.equals(creditTransferResponse.getResponseCode());

                            placementApproval.setApprovalStatus(ApprovalStatus.Approved);
                            placementApproval.setApproverId(approveId);
                            placementApproval.setApproveIPAddress(approveIPAddress);
                            placementApproval.setApproveDate(LocalDateTime.now());
                            placementApproval.setNcbsStatus(isSuccessCreditTransfer ? NCBS_STATUS_SUCCESS : NCBS_STATUS_FAILED);
                            placementApproval.setNcbsResponseCode(creditTransferResponse.getResponseCode());
                            placementApproval.setNcbsResponseMessage(creditTransferResponse.getResponseMessage());
                            placementApprovalRepository.save(placementApproval);

                            if (isSuccessCreditTransfer) {
                                totalSuccess++;
                            } else {
                                errorMessageDTOList.add(new ErrorMessageDTO(placementApproval.getSiReferenceID(), Collections.singletonList("Credit Transfer is failed: " + creditTransferResponse.getResponseMessage())));
                                totalFailed++;
                            }
                        } else {
                            errorMessageDTOList.add(new ErrorMessageDTO(placementApproval.getSiReferenceID(), Collections.singletonList("Inquiry Account is failed: " + inquiryAccountResponse.getResponseMessage())));
                            totalFailed++;
                        }

                    } else if (SKN.equalsIgnoreCase(placementTransferType) || RTGS.equalsIgnoreCase(placementTransferType)) {
                        TransferSknRtgsRequest request = createTransferSknRtgsRequest(placementApproval);
                        saveNCBSRequest(placementApproval, TRANSFER_SKN_RTGS, convertObjectToJson(request));

                        TransferSknRtgsResponse transferSknRtgsResponse = ncbsRequestService.transferSknRtgs(placementApproval.getReferenceId(), request);
                        saveNCBSResponse(placementApproval, transferSknRtgsResponse.getResponseCode(), transferSknRtgsResponse.getResponseMessage(), transferSknRtgsResponse.getSubStatusProvider(), convertObjectToJson(transferSknRtgsResponse));

                        boolean isSuccessTransferSknRtgs = API_RESPONSE_CODE_SUCCESS.equals(transferSknRtgsResponse.getResponseCode());

                        placementApproval.setApprovalStatus(ApprovalStatus.Approved);
                        placementApproval.setApproverId(approveId);
                        placementApproval.setApproveIPAddress(approveIPAddress);
                        placementApproval.setApproveDate(LocalDateTime.now());
                        placementApproval.setNcbsStatus(isSuccessTransferSknRtgs ? NCBS_STATUS_SUCCESS : NCBS_STATUS_FAILED);
                        placementApproval.setNcbsResponseCode(transferSknRtgsResponse.getResponseCode());
                        placementApproval.setNcbsResponseMessage(transferSknRtgsResponse.getResponseMessage());
                        placementApprovalRepository.save(placementApproval);

                        if (isSuccessTransferSknRtgs) {
                            totalSuccess++;
                        } else {
                            errorMessageDTOList.add(new ErrorMessageDTO(placementApproval.getSiReferenceID(), Collections.singletonList("Transfer SKN RTGS is failed: " + transferSknRtgsResponse.getResponseMessage())));
                            totalFailed++;
                        }
                    } else {
                        log.warn("Unsupported placement transfer type: {}", placementTransferType);
                    }
                }
            } catch (Exception e) {
                log.error("Error processing placement transfer for placementId {}: {}", placementApprovalId, e.getMessage(), e);
                errorMessageDTOList.add(new ErrorMessageDTO(placementApprovalId.toString(), Collections.singletonList("Error occurred while processing transfer: " + e.getMessage())));
                totalFailed++;
            }
        }

        return buildPlacementApprovalResponse(totalSuccess, totalFailed, errorMessageDTOList);
    }
```

# Methods

```java
    private void saveNCBSRequest(PlacementApproval placementApproval, String serviceType, String requestJson) {
        CreateNCBSRequest createNCBSRequest = CreateNCBSRequest.builder()
                .createdDate(LocalDateTime.now())
                .placementId(placementApproval.getId().toString())
                .siReferenceId(placementApproval.getSiReferenceID())
                .placementType(placementApproval.getPlacementType())
                .placementProcessType(placementApproval.getPlacementProcessType())
                .placementTransferType(placementApproval.getPlacementTransferType())
                .requestJson(requestJson)
                .referenceId(placementApproval.getReferenceId())
                .serviceType(serviceType)
                .build();
        NCBSRequest ncbsRequest = ncbsRequestService.save(createNCBSRequest);
        log.info("[NCBS Request] Successfully saved with ID: {}", ncbsRequest.getId());
    }

    private void saveNCBSResponse(PlacementApproval placementApproval, String responseCode, String responseMessage, SubStatusProviderDTO subStatusProvider, String responseJson) {
        boolean isSuccess = API_RESPONSE_CODE_SUCCESS.equals(responseCode);
        CreateNCBSResponse createNCBSResponse = CreateNCBSResponse.builder()
                .createdDate(LocalDateTime.now())
                .placementId(placementApproval.getId())
                .siReferenceId(placementApproval.getSiReferenceID())
                .placementType(placementApproval.getPlacementType())
                .placementProcessType(placementApproval.getPlacementProcessType())
                .placementTransferType(placementApproval.getPlacementTransferType())
                .responseCode(responseCode)
                .responseMessage(responseMessage)
                .providerSystem(subStatusProvider.getProviderSystem())
                .statusCode(subStatusProvider.getStatusCode())
                .statusMessage(subStatusProvider.getStatusMessage())
                .responseJson(responseJson)
                .status(isSuccess ? NCBS_STATUS_SUCCESS : NCBS_STATUS_FAILED)
                .build();

        NCBSResponse ncbsResponse = ncbsResponseService.save(createNCBSResponse);
        log.info("[NCBS Response] Successfully saved with id: {}", ncbsResponse.getId());
    }

    private InquiryAccountRequest createInquiryAccountRequest(PlacementApproval placementApproval) {
        return InquiryAccountRequest.builder()
                .sttlmAmt(placementApproval.getPrinciple().toPlainString())
                .sttlmCcy("IDR")
                .chargeBearerCode("DEBT")
                .senderBic("BDINIDJA")
                .senderAcctNo(placementApproval.getAccountDebitNo())
                .benefBic(placementApproval.getBiCode())
                .benefAcctNo(placementApproval.getPlacementBankCashAccountNo())
                .purposeTransaction("51002")
                .build();
    }

    private CreditTransferRequest createCreditTransferBiFastRequest(InquiryAccountDataDTO inquiryAccountDataDTO, PlacementApproval placementApproval) {
        log.info("[Credit Transfer] Placement approval: {}", placementApproval);
        return CreditTransferRequest.builder()
                .trxType("CREDIT_TRANSFER")
                .category("01")
                .sttlmAmt(placementApproval.getPrinciple().toPlainString())
                .sttlmCcy("IDR")
                .sttlmDate(placementApproval.getPlacementDate().toString()) // format must be yyyy-MM-dd
                .feeAmt("2500.00") // fee BiFast
                .chargeBearerCode("DEBT")
                .senderAcctNo(placementApproval.getAccountDebitNo())
                .senderAcctType("SVGS")
                .senderBic("BDINIDJA")
                .benefBic(inquiryAccountDataDTO.getBenefId())
                .benefName(inquiryAccountDataDTO.getBenefAcctName())
                .benefId(inquiryAccountDataDTO.getBenefId())
                .benefAcctNo(inquiryAccountDataDTO.getBenefAcctNo())
                .benefAcctType(inquiryAccountDataDTO.getBenefAcctType())
                .proxyType("")
                .proxyValue("")
                .description("Payment for housing")
                .benefType(inquiryAccountDataDTO.getBenefType())
                .benefResidentStatus(inquiryAccountDataDTO.getBenefResidentStatus())
                .benefCityCode(inquiryAccountDataDTO.getBenefCityCode())
                .purposeTransaction("01")
                .cardNo("")
                .build();
    }

    private OverbookingCasaRequest createOverbookingCasaRequest(PlacementApproval placementApproval) {
        return OverbookingCasaRequest.builder()
                .header(new HeaderDTO("01111"))
                .body(
                        BodyDTO.builder()
                                .acctIdFrom(
                                        AcctIdFromDTO.builder()
                                                .atmCardNo("")
                                                .acctIdF("")
                                                .acctTypeF("")
                                                .costCtrF("9207")
                                                .build()
                                )
                                .acctIdTo(
                                        AcctIdToDTO.builder()
                                                .acctIdT(placementApproval.getAccountDebitNo())
                                                .acctTypeT("") // kalau CASA itu 20
                                                .costCtrT("")
                                                .build()
                                )
                                .xferInfo(
                                        XferInfoDTO.builder()
                                                .xferAmt("dari placement approval") // dari placement approval
                                                .xferdesc1("") // nama reksadana
                                                .xferdesc2("") // nama reksadana juga
                                                .build()
                                )
                                .build()
                )
                .build();
    }

    private TransferSknRtgsRequest createTransferSknRtgsRequest(PlacementApproval placementApproval) {
        return TransferSknRtgsRequest.builder()
                .xferInfoFrom(
                        XferInfoFromDTO.builder()
                                .acctId("")
                                .acctType("") // casa
                                .acctCur("360")
                                .build()
                )
                .bdiXferXFOff(
                        BdiXferXFOffDTO.builder()
                                .bdiXferAmtFrm("")
                                .bdiXferAmtFrmLCE(placementApproval.getPrinciple().toPlainString())
                                .bdiXferAmtTo("")
                                .bdiXferAmtToLCE("")
                                .bdiXferType("") // RTGS: ? SKN: ?
                                .bdiXferCurCode("")
                                .bdiXRateAmt("")
                                .bdiStdRateAmt("")
                                .bdiXReffNumber(placementApproval.getSiReferenceID())
                                .bdiXferBeneficiery(
                                        BdiXferBeneficieryDTO.builder()
                                                .bdiBenfID("")
                                                .bdiBenfAcct("")
                                                .bdiBenfName("")
                                                .bdiBenfAddress("")
                                                .bdiBenStatus("")
                                                .bdiBenCitizen("")
                                                .bankInfo(
                                                        BankInfoDTO.builder()
                                                                .biCode(placementApproval.getBiCode())
                                                                .cocCode("")
                                                                .name("")
                                                                .build()
                                                )
                                                .build()
                                )
                                .bdiXferCostCtr("9207")
                                .bdiFeeAmt("") // SKN 2.900, RTGS 30.000
                                .bdiFeeAmtLCE("")
                                .bdiFeeProcIr("")
                                .bdiXferMemo(
                                        BdiXferMemoDTO.builder()
                                                .bdiFrMemo1("")
                                                .bdiFrMemo2("")
                                                .bdiToMemo1("")
                                                .bdiToMemo2("")
                                                .build()
                                )
                                .transInfo(
                                        TransInfoDTO.builder()
                                                .trn("IFT00000")
                                                .bdiFeeBearerType("")
                                                .build()
                                )
                                .lldInfo("")
                                .build()
                )
                .build();
    }


```

# Check Validation Amount Transaction Type

```java
public CreateTransferPlacementResponse createBulk(CreateTransferPlacementDataListRequest createBulkPlacementListRequest, String inputId, String inputIPAddress) {
    log.info("Start create Bulk transfer placement: {}, {}, {}", createBulkPlacementListRequest.getCreateTransferPlacementDataRequestList(), inputId, inputIPAddress);

    List<CreateTransferPlacementDataRequest> createBulkPlacementRequestList = createBulkPlacementListRequest.getCreateTransferPlacementDataRequestList();
    List<ErrorMessageDTO> errorMessageDTOList = new ArrayList<>();
    int totalSuccess = 0;
    int totalFailed = 0;

    if (createBulkPlacementRequestList.isEmpty()) {
        return CreateTransferPlacementResponse.builder()
                .totalDataSuccess(0)
                .totalDataFailed(0)
                .errorMessageDTOList(Collections.singletonList(
                        ErrorMessageDTO.builder()
                                .code("EMPTY_LIST")
                                .errorMessages(Collections.singletonList("Request list is empty"))
                                .build()))
                .build();
    }

    List<PlacementData> placementDataList = new ArrayList<>();

    for (CreateTransferPlacementDataRequest request : createBulkPlacementRequestList) {
        try {
            PlacementData placementData = placementDataService.getById(request.getId());
            
            // ✅ Validasi amount berdasarkan placementTransferType
            feeParameterService.validateAmount(placementData.getPrinciple(), createBulkPlacementListRequest.getPlacementTransferType());

            placementDataList.add(placementData);
        } catch (FeeNotFoundException | InvalidAmountException e) {
            totalFailed++;
            errorMessageDTOList.add(ErrorMessageDTO.builder()
                    .code(request.getId().toString())
                    .errorMessages(Collections.singletonList(e.getMessage()))
                    .build());
        } catch (Exception e) {
            totalFailed++;
            errorMessageDTOList.add(ErrorMessageDTO.builder()
                    .code(request.getId().toString())
                    .errorMessages(Collections.singletonList("Error fetching PlacementData: " + e.getMessage()))
                    .build());
        }
    }

    if (placementDataList.isEmpty()) {
        return CreateTransferPlacementResponse.builder()
                .totalDataSuccess(totalSuccess)
                .totalDataFailed(totalFailed)
                .errorMessageDTOList(errorMessageDTOList)
                .build();
    }

    List<String> siReferenceIDList = placementDataList.stream()
            .map(PlacementData::getSiReferenceID)
            .collect(Collectors.toList());
    List<String> referenceNoList = placementDataList.stream()
            .map(PlacementData::getReferenceNo)
            .collect(Collectors.toList());

    try {
        PlacementData placementData = placementDataList.get(0);
        BigDecimal totalAmount = placementDataList.stream()
                .map(PlacementData::getPrinciple)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        PlacementApproval placementApproval = PlacementApproval.builder()
                .approvalStatus(ApprovalStatus.Pending)
                .inputerId(inputId)
                .inputDate(LocalDateTime.now())
                .inputIPAddress(inputIPAddress)
                .principle(totalAmount)
                .placementTransferType(validateTransferTypeEnum(createBulkPlacementListRequest.getPlacementTransferType()))
                .referenceId(GenerateUniqueKeyUtil.generateReferenceId())
                .build();

        PlacementApproval save = placementApprovalRepository.save(placementApproval);
        placementDataService.updatePlacementApprovalIdAndStatus(placementData.getId(), save.getId(), save.getApprovalStatus().getStatus());
        totalSuccess++;
    } catch (Exception e) {
        totalFailed++;
        errorMessageDTOList.add(ErrorMessageDTO.builder()
                .code("SAVE_ERROR")
                .errorMessages(Collections.singletonList("Error saving PlacementDepositApproval: " + e.getMessage()))
                .build());
    }

    return CreateTransferPlacementResponse.builder()
            .totalDataSuccess(totalSuccess)
            .totalDataFailed(totalFailed)
            .errorMessageDTOList(errorMessageDTOList)
            .build();
}

```

# Transaction Type

```java
BigDecimal totalAmount = placementDataList.stream()
        .map(PlacementData::getPrinciple)
        .filter(Objects::nonNull)
        .reduce(BigDecimal.ZERO, BigDecimal::add);

// Ambil transactionType dari request
String transactionType = createBulkPlacementListRequest.getPlacementTransferType();

// Cari Fee Parameter berdasarkan transactionType
Optional<FeeParameter> feeParameterOpt = feeParameterRepository.findByTransactionType(transactionType);

if (feeParameterOpt.isPresent()) {
    FeeParameter feeParameter = feeParameterOpt.get();
    BigDecimal minAmount = feeParameter.getMinAmount();
    BigDecimal maxAmount = feeParameter.getMaxAmount();

    // Validasi apakah totalAmount berada dalam rentang yang diperbolehkan
    if (totalAmount.compareTo(minAmount) < 0 || totalAmount.compareTo(maxAmount) > 0) {
        totalFailed++;
        errorMessageDTOList.add(ErrorMessageDTO.builder()
                .code("INVALID_AMOUNT")
                .errorMessages(Collections.singletonList(
                        "Total amount " + totalAmount + " is out of range for " + transactionType +
                                " (Min: " + minAmount + ", Max: " + maxAmount + ")"))
                .build());

        return CreateTransferPlacementResponse.builder()
                .totalDataSuccess(totalSuccess)
                .totalDataFailed(totalFailed)
                .errorMessageDTOList(errorMessageDTOList)
                .build();
    }
} else {
    totalFailed++;
    errorMessageDTOList.add(ErrorMessageDTO.builder()
            .code("TRANSACTION_TYPE_NOT_FOUND")
            .errorMessages(Collections.singletonList("No fee parameter found for transaction type: " + transactionType))
            .build());

    return CreateTransferPlacementResponse.builder()
            .totalDataSuccess(totalSuccess)
            .totalDataFailed(totalFailed)
            .errorMessageDTOList(errorMessageDTOList)
            .build();
}

```

# Check amount and placement transfer type

```java
try {
    PlacementData placementData = placementDataList.get(0);
    BigDecimal totalAmount = placementDataList.stream()
            .map(PlacementData::getPrinciple)
            .filter(Objects::nonNull)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

    String placementTransferType = validateTransferTypeEnum(createBulkPlacementListRequest.getPlacementTransferType());

    // Ambil batasan jumlah berdasarkan placementTransferType
    Optional<FeeParameter> feeParameterOpt = feeParameterRepository.findByTransactionType(placementTransferType);

    if (feeParameterOpt.isPresent()) {
        FeeParameter feeParameter = feeParameterOpt.get();
        BigDecimal minAmount = feeParameter.getMinAmount();
        BigDecimal maxAmount = feeParameter.getMaxAmount();

        if (totalAmount.compareTo(minAmount) < 0 || totalAmount.compareTo(maxAmount) > 0) {
            totalFailed++;
            errorMessageDTOList.add(ErrorMessageDTO.builder()
                    .code("INVALID_AMOUNT")
                    .errorMessages(Collections.singletonList(
                            "Total amount " + totalAmount + " is out of range for " + placementTransferType +
                                    " (Min: " + minAmount + ", Max: " + maxAmount + ")"))
                    .build());

            return CreateTransferPlacementResponse.builder()
                    .totalDataSuccess(totalSuccess)
                    .totalDataFailed(totalFailed)
                    .errorMessageDTOList(errorMessageDTOList)
                    .build();
        }
    } else {
        totalFailed++;
        errorMessageDTOList.add(ErrorMessageDTO.builder()
                .code("TRANSACTION_TYPE_NOT_FOUND")
                .errorMessages(Collections.singletonList("No fee parameter found for transaction type: " + placementTransferType))
                .build());

        return CreateTransferPlacementResponse.builder()
                .totalDataSuccess(totalSuccess)
                .totalDataFailed(totalFailed)
                .errorMessageDTOList(errorMessageDTOList)
                .build();
    }

    // Jika validasi berhasil, lanjutkan penyimpanan
    LocalDateTime now = LocalDateTime.now();
    PlacementApproval placementApproval = PlacementApproval.builder()
            .approvalStatus(ApprovalStatus.Pending)
            .inputerId(inputId)
            .inputDate(now)
            .inputIPAddress(inputIPAddress)
            .approverId("")
            .approveDate(null)
            .approveIPAddress("")
            .imCode(placementData.getImCode())
            .imName(placementData.getImName())
            .fundCode(placementData.getFundCode())
            .fundName(placementData.getFundName())
            .placementBankCode(placementData.getPlacementBankCode())
            .placementBankName(placementData.getPlacementBankName())
            .placementBankCashAccountName(placementData.getPlacementBankCashAccountName())
            .placementBankCashAccountNo(placementData.getPlacementBankCashAccountNo())
            .currency(placementData.getCurrency())
            .principle(totalAmount)
            .placementDate(placementData.getPlacementDate())
            .referenceNo(String.join(", ", referenceNoList))
            .siReferenceID(String.join(", ", siReferenceIDList))
            .accountDebitNo(placementData.getAccountDebitNo())
            .biCode(placementData.getBiCode())
            .placementType(placementData.getPlacementType())
            .placementProcessType(PLACEMENT_TYPE_BULK)
            .placementTransferType(placementTransferType) // Set placementTransferType
            .referenceId(GenerateUniqueKeyUtil.generateReferenceId())
            .ncbsStatus(null)
            .ncbsResponseCode(null)
            .ncbsResponseMessage(null)
            .build();

    PlacementApproval save = placementApprovalRepository.save(placementApproval);
    PlacementData placementDataUpdated = placementDataService.updatePlacementApprovalIdAndStatus(
            placementData.getId(), save.getId(), save.getApprovalStatus().getStatus());

    log.info("[Bulk] Placement deposit approval id: {}, placement data id: {}",
            placementDataUpdated.getPlacementApprovalId(), placementDataUpdated.getId());

    totalSuccess++;
} catch (Exception e) {
    totalFailed++;
    errorMessageDTOList.add(ErrorMessageDTO.builder()
            .code("SAVE_ERROR")
            .errorMessages(Collections.singletonList("Error saving PlacementDepositApproval: " + e.getMessage()))
            .build());
}

return CreateTransferPlacementResponse.builder()
        .totalDataSuccess(totalSuccess)
        .totalDataFailed(totalFailed)
        .errorMessageDTOList(errorMessageDTOList)
        .build();

```

- Service Payment Status -> API Sendiri
- add column payUserRefNo in the NCBS Response, only BI-FAST
- Update NCBS Status Failed or Success in the table PlacementApproval -> create API update

# Flying Saucer 9.4.1
```xml
<!-- https://mvnrepository.com/artifact/org.xhtmlrenderer/flying-saucer-pdf -->
<dependency>
    <groupId>org.xhtmlrenderer</groupId>
    <artifactId>flying-saucer-pdf</artifactId>
    <version>9.4.1</version>
</dependency>

```

# Update Check Response Code

```java
private PlacementApprovalResult finalizeApproval(PlacementApproval placementApproval, String approveId, String approveIPAddress, String responseCode, String responseMessage, List<ErrorMessageDTO> errorMessageDTOList, String processName) {
    // Validasi objek yang mungkin null
    if (placementApproval == null || responseCode == null) {
        throw new IllegalArgumentException("PlacementApproval or responseCode cannot be null");
    }

    boolean isSuccess = API_RESPONSE_CODE_SUCCESS.equals(responseCode);
    boolean isInsufficientBalance = checkIfInsufficientBalance(responseCode);

    // Set common fields for all cases
    placementApproval.setApproverId(approveId);
    placementApproval.setApproveIPAddress(approveIPAddress);
    placementApproval.setApproveDate(LocalDateTime.now());
    placementApproval.setNcbsResponseCode(responseCode);
    placementApproval.setNcbsResponseMessage(responseMessage);

    if (isSuccess) {
        handleSuccess(placementApproval);
    } else if (isInsufficientBalance) {
        handleInsufficientBalance(placementApproval);
    } else {
        handleFailure(placementApproval);
    }

    // Save the updated placementApproval
    placementApprovalRepository.save(placementApproval);

    // Update PlacementData table
    updatePlacementData(placementApproval, isInsufficientBalance);

    // Add error message if not successful
    if (!isSuccess) {
        errorMessageDTOList.add(new ErrorMessageDTO(
                placementApproval.getSiReferenceId(), 
                Collections.singletonList(processName + " failed: " + responseMessage)
        ));
    }

    return PlacementApprovalResult.builder()
            .success(isSuccess)
            .errorMessages(errorMessageDTOList)
            .build();
}

// Method to check if responseCode indicates insufficient balance
private boolean checkIfInsufficientBalance(String responseCode) {
    List<String> insufficientBalanceCodes = responseCodeRepository.findAllByName("INSUFFICIENT_BALANCE")
            .stream()
            .map(ResponseCode::getCode)
            .collect(Collectors.toList());
    return insufficientBalanceCodes.contains(responseCode);
}

// Method to handle success case
private void handleSuccess(PlacementApproval placementApproval) {
    placementApproval.setApprovalStatus(ApprovalStatus.Approved);
    placementApproval.setNcbsStatus(NCBS_STATUS_SUCCESS);
}

// Method to handle insufficient balance case
private void handleInsufficientBalance(PlacementApproval placementApproval) {
    placementApproval.setApprovalStatus(ApprovalStatus.Approved);
    placementApproval.setNcbsStatus(NCBS_STATUS_SUCCESS);
}

// Method to handle failure case
private void handleFailure(PlacementApproval placementApproval) {
    placementApproval.setApprovalStatus(ApprovalStatus.Approved); // Apakah ini seharusnya Approved atau Rejected?
    placementApproval.setNcbsStatus(NCBS_STATUS_FAILED);
}

// Method to update PlacementData table
private void updatePlacementData(PlacementApproval placementApproval, boolean isInsufficientBalance) {
    List<PlacementData> placementDataList = placementDataRepository.findByPlacementApprovalId(placementApproval.getId().toString());
    if (placementDataList != null && !placementDataList.isEmpty()) {
        placementDataList.forEach(placementData -> {
            if (placementData != null) {
                placementData.setPlacementApprovalStatus(placementApproval.getApprovalStatus().getStatus());
                if (isInsufficientBalance) {
                    placementData.setPlacementApprovalId(""); // Set to empty string for insufficient balance case
                }
            }
        });
        placementDataRepository.saveAll(placementDataList);
    }
}
```

# Enum

```java
public enum TransactionType {
    SKN("3"),
    RTGS("1");

    private final String code;

    TransactionType(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public static TransactionType fromString(String value) {
        for (TransactionType type : TransactionType.values()) {
            if (type.name().equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown transaction type: " + value);
    }
}
```

```java
FeeParameterPlacement feeParameterPlacement = feeParameterPlacementRepository.findByTransactionType(placementApproval.getPlacementTransferType())
        .orElseThrow(() -> new DataNotFoundException("Fee parameter placement not found with transaction type: " + placementApproval.getPlacementTransferType()));

String transactionType = feeParameterPlacement.getTransactionType();
String transactionTypeCode;

try {
    TransactionType type = TransactionType.fromString(transactionType);
    transactionTypeCode = type.getCode();
} catch (IllegalArgumentException e) {
    throw new DataNotFoundException("Invalid transaction type: " + transactionType);
}
```

    // Method to handle insufficient balance case
    private void handleInsufficientBalance(PlacementApproval placementApproval) {
        placementApproval.setApprovalStatus(Approved);
        placementApproval.setNcbsStatus(NCBS_STATUS_SUCCESS);
    }

## Example Response

```java
public Mono<OverbookingCasaResponse> overbookingCasa(String referenceId, OverbookingCasaRequest request) {
    return webClient.post()
            .uri(overbookingCasaUrl)
            .contentType(MediaType.APPLICATION_JSON)
            .header("Authorization", "Bearer token123")
            .bodyValue(request)
            .exchangeToMono(response -> {
                // 1. Ekstrak semua headers
                HttpHeaders headers = response.headers().asHttpHeaders();
                
                // 2. Bangun HeaderResponseDTO dari headers
                HeaderResponseDTO headerResponse = HeaderResponseDTO.builder()
                        .correlationId(headers.getFirst("X-CorrelationID"))
                        .serviceCode(headers.getFirst("svccode"))
                        .serviceRequestId(headers.getFirst("svcrqid"))
                        .statusCodeResponse(String.valueOf(response.statusCode().value()))
                        .build();
                
                // 3. Handle response body
                return response.bodyToMono(JsonNode.class) // Parse as JsonNode untuk fleksibilitas
                        .map(jsonBody -> {
                            // 4. Map response JSON ke object OverbookingCasaResponse
                            OverbookingCasaResponse responseObj = OverbookingCasaResponse.builder()
                                    .responseCode(jsonBody.path("responseCode").asText())
                                    .responseMessage(jsonBody.path("responseMessage").asText())
                                    .headerResponse(headerResponse)
                                    .build();
                            
                            // 5. Handle subStatusProvider jika ada
                            if (jsonBody.has("subStatusProvider")) {
                                SubStatusProviderDTO subStatus = SubStatusProviderDTO.builder()
                                        .providerSystem(jsonBody.path("subStatusProvider").path("providerSystem").asText())
                                        .statusCode(jsonBody.path("subStatusProvider").path("statusCode").asText())
                                        .statusDesc(jsonBody.path("subStatusProvider").path("statusDesc").asText())
                                        .build();
                                responseObj.setSubStatusProvider(subStatus);
                            }
                            
                            // 6. Handle data jika ada
                            if (jsonBody.has("data")) {
                                OverbookingCasaDataDTO data = OverbookingCasaDataDTO.builder()
                                        .traceId(jsonBody.path("data").path("traceId").asText())
                                        .build();
                                responseObj.setData(data);
                            }
                            
                            return responseObj;
                        });
            })
            .onErrorResume(e -> Mono.just(
                    OverbookingCasaResponse.builder()
                            .responseCode("500")
                            .responseMessage("Error processing request: " + e.getMessage())
                            .build()
            ));
}
```

## Response New

```java
public Mono<OverbookingCasaResponse> overbookingCasa(String referenceId, OverbookingCasaRequest request) {
    return webClient.post()
            .uri(overbookingCasaUrl)
            .contentType(MediaType.APPLICATION_JSON)
            .header("Authorization", "Bearer token123")
            .bodyValue(request)
            .exchangeToMono(response -> {
                // 1. Ekstrak headers
                HttpHeaders headers = response.headers().asHttpHeaders();
                
                // 2. Build HeaderResponseDTO
                HeaderResponseDTO headerResponse = HeaderResponseDTO.builder()
                        .correlationId(headers.getFirst("X-CorrelationID"))
                        .serviceCode(headers.getFirst("svccode"))
                        .serviceRequestId(headers.getFirst("svcrqid"))
                        .statusCodeResponse(String.valueOf(response.statusCode().value()))
                        .build();
                
                // 3. Direct mapping to OverbookingCasaResponse
                return response.bodyToMono(OverbookingCasaResponse.class)
                        .map(body -> {
                            body.setHeaderResponse(headerResponse);
                            return body;
                        });
            })
            .onErrorResume(e -> Mono.just(
                    OverbookingCasaResponse.builder()
                            .responseCode("500")
                            .responseMessage("Error: " + e.getMessage())
                            .build()
            ));
}
```

## Response New 1

```java
public Mono<OverbookingCasaResponse> overbookingCasa(String referenceId, OverbookingCasaRequest request) {
    return webClient.post()
            .uri(overbookingCasaUrl)
            .contentType(MediaType.APPLICATION_JSON)
            // ... (headers lainnya)
            .exchangeToMono(response -> {
                // 1. Log semua headers
                HttpHeaders httpHeaders = response.headers().asHttpHeaders();
                httpHeaders.forEach((key, value) -> 
                    log.info("[Overbooking Casa] Header Response: {} = {}", key, value));
                
                // 2. Log status code
                HttpStatus statusCode = response.statusCode();
                log.info("[Overbooking Casa] Status Code Response: {}", statusCode);
                
                // 3. Build HeaderResponseDTO dengan status code
                HeaderResponseDTO headerResponse = HeaderResponseDTO.builder()
                        .correlationId(httpHeaders.getFirst("X-CorrelationID"))
                        .serviceCode(httpHeaders.getFirst("svccode"))
                        .serviceRequestId(httpHeaders.getFirst("svcrqid"))
                        .statusCodeResponse(String.valueOf(statusCode.value())) // Convert ke String
                        .build();
                
                // 4. Handle response body
                return response.bodyToMono(OverbookingCasaResponse.class)
                        .map(body -> {
                            body.setHeaderResponse(headerResponse);
                            return body;
                        })
                        .switchIfEmpty(Mono.just(
                            OverbookingCasaResponse.builder()
                                .responseCode(String.valueOf(statusCode.value()))
                                .responseMessage("Empty response body")
                                .headerResponse(headerResponse)
                                .build()
                        ));
            })
            .onErrorResume(e -> {
                log.error("[Overbooking Casa] Error: ", e);
                return Mono.just(
                    OverbookingCasaResponse.builder()
                        .responseCode("500")
                        .responseMessage("Service error: " + e.getMessage())
                        .build()
                );
            });
}
```

## Response New 2

```java
public Mono<OverbookingCasaResponse> overbookingCasa(String referenceId, OverbookingCasaRequest request) {
    return webClient.post()
            .uri(overbookingCasaUrl)
            .contentType(MediaType.APPLICATION_JSON)
            // ... (headers lainnya)
            .exchangeToMono(response -> {
                // 1. Log semua headers
                HttpHeaders httpHeaders = response.headers().asHttpHeaders();
                httpHeaders.forEach((key, value) -> 
                    log.info("[Overbooking Casa] Header Response: {} = {}", key, value));
                
                // 2. Log status code
                HttpStatus statusCode = response.statusCode();
                log.info("[Overbooking Casa] Status Code Response: {}", statusCode);
                
                // 3. Build HeaderResponseDTO dengan status code
                HeaderResponseDTO headerResponse = HeaderResponseDTO.builder()
                        .correlationId(httpHeaders.getFirst("X-CorrelationID"))
                        .serviceCode(httpHeaders.getFirst("svccode"))
                        .serviceRequestId(httpHeaders.getFirst("svcrqid"))
                        .statusCodeResponse(String.valueOf(statusCode.value())) // Convert ke String
                        .build();
                
                // 4. Handle response body
                return response.bodyToMono(OverbookingCasaResponse.class)
                        .map(body -> {
                            body.setHeaderResponse(headerResponse);
                            return body;
                        })
                        .switchIfEmpty(Mono.just(
                            OverbookingCasaResponse.builder()
                                .responseCode(String.valueOf(statusCode.value()))
                                .responseMessage("Empty response body")
                                .headerResponse(headerResponse)
                                .build()
                        ));
            })
            .onErrorResume(e -> {
                log.error("[Overbooking Casa] Error: ", e);
                return Mono.just(
                    OverbookingCasaResponse.builder()
                        .responseCode("500")
                        .responseMessage("Service error: " + e.getMessage())
                        .build()
                );
            });
}
```

```java
      .exchangeToMono(response -> {
                    // Log semua headers
                    HttpHeaders httpHeaders = response.headers().asHttpHeaders();
                    httpHeaders.forEach((key, value) ->
                            log.info("[Overbooking Casa] Header Response: {} = {}", key, value));

                    // Log status code
                    HttpStatus statusCode = response.statusCode();
                    log.info("[Overbooking Casa] Status Code Response: {}", statusCode);

                    // Build HeaderResponseDTO dengan status code
                    HeaderResponseDTO headerResponse = HeaderResponseDTO.builder()
                            .correlationId(httpHeaders.getFirst(CORRELATION_ID))
                            .channelId(httpHeaders.getFirst(CHANNEL_ID))
                            .date(httpHeaders.getFirst(DATE))
                            .providerSystem(httpHeaders.getFirst(PROVIDER_SYSTEM))
                            .serviceCode(httpHeaders.getFirst(SERVICE_CODE))
                            .serviceRequestId(httpHeaders.getFirst(SERVICE_REQUEST_ID))
                            .via(httpHeaders.getFirst(HttpHeaders.VIA))
                            .statusCodeResponse(String.valueOf(statusCode))
                            .build();
                    // Handle response body
                    return response.bodyToMono(OverbookingCasaResponse.class)
                            .map(body -> {
                                body.setHeaderResponse(headerResponse);
                                return body;
                            })
                            .switchIfEmpty(Mono.just(
                                    OverbookingCasaResponse.builder()
                                            .headerResponse(headerResponse)
                                            .build()
                            ));
                })
                .onErrorResume(e -> {
                    log.error("[Overbooking Casa] Error: ", e);
                    return Mono.just(
                            OverbookingCasaResponse.builder()
                                    .responseCode("500")
                                    .responseMessage("Service error: " + e.getMessage())
                                    .build()
                    );
                });
```


```java
    @Override
    public OverbookingCasaResponse overbookingCasa(String referenceId, OverbookingCasaRequest request) {
        log.info("[Overbooking Casa] Start - ReferenceId: {}", referenceId);
        try {
            OverbookingCasaResponse response = middlewareService.overbookingCasa(referenceId, request)
                    .block(Duration.ofSeconds(30));
            log.info("[Overbooking Casa] Success - ReferenceId: {}", referenceId);
            return response;
        } catch (WebClientResponseException e) {
            log.error("[Overbooking Casa] Server Error - ReferenceId: {} - Status: {} - Body: {}",
                    referenceId, e.getStatusCode(), e.getResponseBodyAsString(), e);
            throw new GeneralException("Server error: " + e.getStatusCode());
        } catch (Exception e) {
            log.error("[Overbooking Casa] Failed - ReferenceId: {} - Error: {}",
                    referenceId, e.getMessage(), e);
            throw new GeneralException("Request failed: " + e.getMessage());
        }
    }
```

```java
@Override
public OverbookingCasaResponse overbookingCasa(String referenceId, OverbookingCasaRequest request) {
    final String operationName = "Overbooking CASA";
    log.info("[{}] Start - ReferenceId: {}", operationName, referenceId);
    
    try {
        // Execute reactive call with proper threading and timeout
        OverbookingCasaResponse response = middlewareService.overbookingCasa(referenceId, request)
            .subscribeOn(Schedulers.boundedElastic())  // Isolate blocking call
            .timeout(Duration.ofSeconds(30))           // Apply timeout at reactive level
            .doOnSuccess(res -> log.info("[{}] Success - ReferenceId: {}", operationName, referenceId))
            .doOnError(e -> log.error("[{}] Error during execution - ReferenceId: {}", operationName, referenceId, e))
            .block();  // Block only after all reactive configurations are set

        return response;
    } catch (WebClientResponseException e) {
        String errorMsg = String.format("[%s] Server Error - ReferenceId: %s - Status: %s - Body: %s",
            operationName, referenceId, e.getStatusCode(), e.getResponseBodyAsString());
        log.error(errorMsg, e);
        throw new GeneralException(errorMsg, e);
    } catch (RuntimeException e) {
        if (e.getCause() instanceof TimeoutException) {
            String timeoutMsg = String.format("[%s] Timeout - ReferenceId: %s", operationName, referenceId);
            log.error(timeoutMsg, e);
            throw new GeneralException(timeoutMsg, e);
        }
        String errorMsg = String.format("[%s] Failed - ReferenceId: %s", operationName, referenceId);
        log.error(errorMsg, e);
        throw new GeneralException(errorMsg, e);
    } finally {
        log.info("[{}] Completed - ReferenceId: {}", operationName, referenceId);
    }
}
```

```java
public Mono<OverbookingCasaResponse> overbookingCasa(String referenceId, OverbookingCasaRequest request) 
    throws JsonProcessingException, NoSuchAlgorithmException, InvalidKeyException {
    
    final String operationName = "Overbooking CASA";
    final String requestId = referenceId; // Alias for clarity in logs
    
    // 1. Prepare request components reactively
    return Mono.fromCallable(() -> {
            String jsonBody = objectMapper.writeValueAsString(request);
            return jsonBody.replaceAll("\\s", "");
        })
        .subscribeOn(Schedulers.boundedElastic()) // Offload blocking JSON processing
        .flatMap(processedRequestBody -> {
            String timestamp = getCurrentTimestamp();
            String dataToSign = timestamp + processedRequestBody;
            String signature = computeHMACSHA512(dataToSign, secretKey);

            // 2. Log request details
            logRequest(operationName, requestId, processedRequestBody, timestamp, signature);

            // 3. Execute web client call
            return webClient.post()
                .uri(overbookingCasaUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .headers(headers -> buildHeaders(headers, requestId, timestamp, signature))
                .bodyValue(request)
                .exchangeToMono(this::handleResponse)
                .onErrorResume(e -> handleError(e, operationName, requestId));
        });
}

// Helper method for header construction
private void buildHeaders(HttpHeaders headers, String referenceId, String timestamp, String signature) {
    headers.add(BDI_KEY, apiBDIKey);
    headers.add(BDI_EXTERNAL_ID, referenceId);
    headers.add(BDI_TIMESTAMP, timestamp);
    headers.add(BDI_CHANNEL, overbookingCasaBDIChannel);
    headers.add(BDI_SERVICE_CODE, overbookingCasaBDIServiceCode);
    headers.add(BDI_SIGNATURE, signature);
}

// Helper method for response handling
private Mono<OverbookingCasaResponse> handleResponse(ClientResponse response) {
    HttpHeaders headers = response.headers().asHttpHeaders();
    HttpStatus status = response.statusCode();
    
    // Log response details
    headers.forEach((k, v) -> 
        log.debug("[Overbooking CASA] Response Header: {} = {}", k, v));
    log.info("[Overbooking CASA] Response Status: {}", status);
    
    HeaderResponseDTO headerResponse = getHeaderResponseDTO(headers, status);
    
    return response.bodyToMono(OverbookingCasaResponse.class)
        .defaultIfEmpty(OverbookingCasaResponse.builder().build())
        .map(body -> {
            body.setHeaderResponse(headerResponse);
            return body;
        });
}

// Helper method for error handling
private Mono<OverbookingCasaResponse> handleError(Throwable e, String operationName, String requestId) {
    log.error("[{}] Error for RequestID: {} - Error: {}", 
        operationName, requestId, e.getMessage(), e);
    
    return Mono.just(
        OverbookingCasaResponse.builder()
            .responseCode("500")
            .responseMessage(String.format("[%s] Service error - Ref: %s", operationName, requestId))
            .errorDetails(e.getMessage())
            .build()
    );
}

// Helper method for request logging
private void logRequest(String operation, String requestId, String body, 
    String timestamp, String signature) {
    
    log.info("[{}] Initiating request - Ref: {}", operation, requestId);
    log.debug("[{}] Request Body: {}", operation, body);
    log.debug("[{}] Timestamp: {}", operation, timestamp);
    log.debug("[{}] Signature: {}", operation, signature.substring(0, 8) + "..."); // Partial log
}
```

```java
public OverbookingCasaResponse overbookingCasa(String referenceId, OverbookingCasaRequest request) {
    final String operationName = "Overbooking CASA";
    final Duration timeout = Duration.ofSeconds(30);
    
    log.info("[{}] Start - ReferenceId: {}", operationName, referenceId);
    final long startTime = System.currentTimeMillis();

    try {
        OverbookingCasaResponse response = middlewareService.overbookingCasa(referenceId, request)
            .subscribeOn(Schedulers.boundedElastic())
            .timeout(timeout)
            .doOnSuccess(res -> 
                log.info("[{}] Success - ReferenceId: {} - Duration: {}ms", 
                    operationName, referenceId, System.currentTimeMillis() - startTime))
            .doOnError(e -> 
                log.error("[{}] Error during execution - ReferenceId: {}", 
                    operationName, referenceId, e))
            .doFinally(signalType -> 
                log.debug("[{}] Operation signal - Ref: {} - Signal: {}", 
                    operationName, referenceId, signalType))
            .block(timeout);  // Apply same timeout at blocking level

        return response;
    } catch (WebClientResponseException e) {
        String errorMsg = String.format("[%s] Server Error - ReferenceId: %s - Status: %s",
            operationName, referenceId, e.getStatusCode());
        log.error("{} - Response Body: {}", errorMsg, e.getResponseBodyAsString(), e);
        throw new GeneralException(errorMsg, e);
    } catch (RuntimeException e) {
        handleSpecificExceptions(operationName, referenceId, e);
        throw new GeneralException(String.format("[%s] Failed - ReferenceId: %s", 
            operationName, referenceId), e);
    } finally {
        log.info("[{}] Completed - ReferenceId: {} - Total Duration: {}ms", 
            operationName, referenceId, System.currentTimeMillis() - startTime);
    }
}

private void handleSpecificExceptions(String operationName, String referenceId, RuntimeException e) {
    if (e.getCause() instanceof TimeoutException) {
        log.error("[{}] Timeout - ReferenceId: {}", operationName, referenceId, e);
        throw new GeneralException(String.format("[%s] Timeout - ReferenceId: %s", 
            operationName, referenceId), e);
    }
    if (e instanceof ReactorBlockingOperationException) {
        log.error("[{}] Blocking Operation Error - ReferenceId: {}", 
            operationName, referenceId, e);
    }
}
```

# Thread Pool Configuration

```java
package com.services.billingservice.config.placement;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.CustomizableThreadFactory;

import java.util.concurrent.*;

@Configuration
public class ThreadPoolConfiguration {

    // 1. Single-thread pool (untuk task sequential)
    @Bean(name = "singleThreadExecutor")
    public ExecutorService singleThreadExecutor() {
        return Executors.newSingleThreadExecutor(
                new CustomizableThreadFactory("single-thread-"));
    }

    // 2. Multi-thread pool (untuk parallel processing)
    @Bean(name = "multiThreadExecutor")
    public ExecutorService multiThreadExecutor() {
        return new ThreadPoolExecutor(
                4, // corePoolSize
                8, // maxPoolSize
                60L, TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(100),
                new CustomizableThreadFactory("multi-thread-")
        );
    }

    // 3. Cached thread pool (untuk I/O-bound tasks)
    @Bean(name = "cachedThreadExecutor")
    public ExecutorService cachedThreadExecutor() {
        return Executors.newCachedThreadPool(
                new CustomizableThreadFactory("cached-thread-")
        );
    }

}
```

# Thread Pool Shutdown Manager

```java
@Component
public class ThreadPoolShutdownManager implements DisposableBean {

    private final List<ExecutorService> executors;

    public ThreadPoolShutdownManager(
            @Qualifier("multiThreadExecutor") ExecutorService multiThreadPool,
            @Qualifier("singleThreadExecutor") ExecutorService singleThreadPool
    ) {
        this.executors = Arrays.asList(multiThreadPool, singleThreadPool);
    }

    @Override
    public void destroy() throws Exception {
        for (ExecutorService executor : executors) {
            executor.shutdown();
            if (!executor.awaitTermination(15, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        }
    }

}
```

netstat -ano | findstr :9090


# Update S-Invest
```java
@Service
@Slf4j
@RequiredArgsConstructor
public class InstructionsSInvestService {
    private final Semaphore uploadSemaphore = new Semaphore(1); // Hanya 1 request yang diproses
    private final InstructionSInvestMapper instructionSInvestMapper;
    // other dependencies...

    public InstructionsSInvestResponse uploadData(UploadInstructionsSInvestListRequest request, 
                                               PlacementDataChangeDTO placementDataChangeDTO) {
        if (!uploadSemaphore.tryAcquire()) {
            throw new SystemBusyException("System sedang memproses upload data lainnya. Silakan coba beberapa saat lagi");
        }

        try {
            log.info("Start upload data Instructions S Invest: {}, {}", request, placementDataChangeDTO);
            int totalDataSuccess = 0;
            int totalDataFailed = 0;
            List<ErrorMessageDTO> errorMessageDTOList = new ArrayList<>();

            for (UploadInstructionsSInvestDataRequest dataRequest : request.getUploadInstructionsSInvestDataRequestList()) {
                try {
                    // Validasi tanggal
                    LocalDate requestDate = LocalDate.parse(dataRequest.getPlacementDate());
                    if (!requestDate.isEqual(LocalDate.now())) {
                        addErrorMessage(dataRequest.getSiReferenceId(), 
                            "Placement Date does not match current date",
                            errorMessageDTOList);
                        totalDataFailed++;
                        continue;
                    }

                    ResultGeneric<InstructionsSInvest> violationResult = getViolationResult(dataRequest);

                    if (violationResult.hasViolations()) {
                        handleViolations(dataRequest, violationResult, errorMessageDTOList);
                        totalDataFailed++;
                    } else {
                        processValidRequest(dataRequest, placementDataChangeDTO, 
                                         errorMessageDTOList, totalDataSuccess, totalDataFailed);
                    }
                } catch (Exception e) {
                    log.error("Error processing SI Reference {}: {}", 
                            dataRequest.getSiReferenceId(), e.getMessage());
                    addErrorMessage(dataRequest.getSiReferenceId(), 
                            "Internal server error: " + e.getMessage(),
                            errorMessageDTOList);
                    totalDataFailed++;
                }
            }
            
            return new InstructionsSInvestResponse(totalDataSuccess, totalDataFailed, errorMessageDTOList);
        } finally {
            uploadSemaphore.release(); // Pastikan semaphore selalu direlease
        }
    }

    private void processValidRequest(UploadInstructionsSInvestDataRequest dataRequest,
                                   PlacementDataChangeDTO placementDataChangeDTO,
                                   List<ErrorMessageDTO> errorMessageDTOList,
                                   int totalDataSuccess, int totalDataFailed) {
        InstructionsSInvestDTO dto = instructionSInvestMapper.fromUploadRequestToDTO(dataRequest);
        
        if (isDuplicate(dto)) {
            addErrorMessage(dto.getSiReferenceId(), 
                          "SI Reference is already exists",
                          errorMessageDTOList);
            totalDataFailed++;
        } else {
            try {
                handleNewInstructionsSInvest(dto, placementDataChangeDTO);
                totalDataSuccess++;
            } catch (Exception e) {
                addErrorMessage(dto.getSiReferenceId(), 
                              "Failed to save: " + e.getMessage(),
                              errorMessageDTOList);
                totalDataFailed++;
            }
        }
    }

    private void addErrorMessage(String siReferenceId, String message, 
                               List<ErrorMessageDTO> errorList) {
        errorList.add(new ErrorMessageDTO(siReferenceId, Collections.singletonList(message)));
    }

    // ... (other existing methods)
}
```
