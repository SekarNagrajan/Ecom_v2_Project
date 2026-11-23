# Version 2 Alignment – Impact Evaluation

This document evaluates the impact of adopting the V2 API behaviour (and DTO design) for booking list/summary endpoints, including breaking changes for consumers and recommendations.

---

## 1. What “Version 2 alignment” means

Alignment here is: **list/summary responses follow the V2 contract** — i.e. clients use the paged V2 list (`GET /api/v2/bookings`) and its DTOs instead of (or in addition to) the V1 summary (`GET /summary`).

| Aspect | V1 (summary list) | V2 (paged list) |
|--------|--------------------|------------------|
| **Booking status** | `BookingStatus` enum → code in JSON | Same (enum → code) |
| **Status description** | `statusDesc` (String) present | **Not exposed** in list item |
| **BL status** | `blStatus` (String) | `BlStatus` enum → code in JSON |

---

## 2. Impact summary

| Change | Type | Severity | Who is affected |
|--------|------|----------|------------------|
| No `statusDesc` in list | Breaking | **High** for UI that shows labels | Any client that displays “Submitted”, “Rejected”, etc. from the list |
| `blStatus` as enum code | Breaking | **Medium** | Clients that assume free-form string or different values |
| Pagination & filter shape | Behavioural | Medium | Clients moving from V1 summary to V2 list |
| Response wrapper & field names | Breaking | Low–Medium | Clients that parse `bookings` vs `items` and different field names |

---

## 3. Detailed impact

### 3.1 Status description (`statusDesc`) not in V2 list

**V1:** `BookingSummaryResponseDto` has both `status` (e.g. `"S"`) and `statusDesc` (e.g. `"Submitted"`).  
**V2:** `PagedBookingListItemV2` has only `status` (code). There is no `statusDesc` (or equivalent) in the list item.

**Impact:**

- **UI / UX:** Any client that shows a “Status” column using `statusDesc` (e.g. “Submitted”, “Rejected”, “ECOM Draft”) will no longer get that string from the list API. They must either:
  - Derive labels from `status` via a local mapping (e.g. S → “Submitted”, V → “Rejected”), or
  - Call another endpoint (e.g. get-by-id) to get a description, which is inefficient for list views.
- **Localisation:** If `statusDesc` was ever localised server-side, that is lost for the list; clients must implement their own mapping and localisation.
- **Consistency:** Mixed display (e.g. some rows “V”, others “Submitted”) usually comes from mixing two sources (e.g. code in one place, description in another). Aligning to “code only” in the list improves consistency but shifts all “human label” logic to the client.

**Recommendation:**  
If V2 is the primary list API, consider adding a `statusDesc` (or `statusDescription`) field to `PagedBookingListItemV2`, populated from the same source as V1 (e.g. `BookingSummaryResponseDto.getStatusDesc()` or `BookingStatus.getDescription()`). That preserves behaviour for UIs that need a single, human-readable status in the list without breaking the enum-based `status` field.

---

### 3.2 BL status: String → enum code

**V1:** `blStatus` is a raw `String` from the database.  
**V2:** `blStatus` is a `BlStatus` enum and is serialised as its code (`"D"`, `"C"`, `"I"`, `"V"`).

**Impact:**

- **Data shape:** Values are now constrained to the four enum values. Any DB value that is not one of these is today converted in the mapper (e.g. `BlStatus.fromValue` can throw; the code path sets `null` on failure). So invalid or legacy values become `null` in V2 instead of an arbitrary string.
- **Clients:** Clients that:
  - Compare or display `blStatus` as an opaque string may still work if the DB only ever had D/C/I/V.
  - Expect other strings (e.g. legacy codes, different casing) will see `null` or need to handle only the four codes.
- **Benefits:** Clearer contract, better validation, and consistent behaviour with other enums (e.g. `BookingStatus`).

**Recommendation:**  
Document that V2 list returns only `"D"`, `"C"`, `"I"`, `"V"` (or `null`), and that any other value is normalised or dropped. If the DB can contain other values, document how they map (e.g. mapped to one of D/C/I/V or to `null`).

---

### 3.3 Booking status: enum → code (unchanged)

Both V1 and V2 use `BookingStatus` with `@JsonValue` so the list always returns the **code** (e.g. `"D"`, `"S"`, `"V"`), not the description. So “V2 alignment” does not change how booking status is represented; the only difference is that V2 list does not also expose `statusDesc`.

---

### 3.4 Pagination, filtering, and response shape

**V1 summary:** Single list, no pagination; response wrapper is e.g. `BookingSummaryListResponse` with `bookings`.  
**V2 list:** Paginated (`offset`, `limit`), with filter and sort; response is `PagedBookingListV2` with `items` and pagination meta (e.g. total count).

**Impact:**

- Clients must use the new query parameters and the `items` + meta structure.
- Migration from “get all” to “get page” requires handling multiple pages and total count if the UI needs it.
- Filtering/sorting are an improvement but require clients to adopt the new contract.

---

## 4. Who is affected

- **Frontends / UIs** that today use:
  - `statusDesc` for display in booking list or filters → **high impact** (field missing in V2 list).
  - `blStatus` as a free-form string → **medium impact** (now enum code or null).
- **Integrations / API consumers** that parse the list response: impact depends on whether they rely on `bookings` vs `items`, `statusDesc`, or raw `blStatus` strings.
- **Backend:** No structural change required for “alignment” itself; impact is on client contracts and documentation.

---

## 5. Recommendations

1. **Add `statusDesc` to V2 list item**  
   Populate it from the same source as V1 (e.g. from `BookingSummaryResponseDto` in `convertToListItem`). This removes the main UX/breaking gap while keeping `status` as the single source of truth (enum code).

2. **Document V2 list contract**  
   Explicitly state:
   - `status`: always one of the `BookingStatus` codes (e.g. D, S, P, C, L, V, T, I).
   - `statusDesc` (if added): human-readable string, optional.
   - `blStatus`: one of D, C, I, V, or null.

3. **Fix OpenAPI example**  
   In `PagedBookingListItemV2`, change the schema example from `"status": "Draft"` to `"status": "D"` so it matches the actual JSON (code only).

4. **Migration path for clients**  
   - Prefer V2 list for new features (pagination, filters, enums).
   - If V1 summary is deprecated, give clients a period where both are supported and document the mapping (e.g. V1 `statusDesc` ↔ client-side mapping from V2 `status` or from a new V2 `statusDesc` if added).

5. **Database / SP consistency**  
   Ensure the stored procedure used for the list always returns the **code** in the `status` column (e.g. `"S"` not `"Submitted"`). If the SP sometimes returns a description in `status`, normalise it to code (or a dedicated column) so that `safeBookingStatus` and enum mapping stay correct.

---

## 6. Summary table

| Area | Impact of V2 alignment | Mitigation |
|------|------------------------|------------|
| **statusDesc missing** | High for list UIs that show labels | Add `statusDesc` to `PagedBookingListItemV2` and populate from summary DTO or enum description |
| **blStatus as enum** | Medium (constrained values, possible null) | Document allowed values and null; confirm DB only uses D/C/I/V or define mapping |
| **Pagination / response shape** | Medium (different API shape) | Document V2 list contract; keep V1 available during migration if needed |
| **Booking status (code)** | No change | Already aligned; optional doc fix for schema example |

Applying Version 2 alignment improves consistency and typing but removes the only server-provided human-readable status in the list. Adding `statusDesc` to the V2 list item is the single change that most reduces impact while keeping the rest of the V2 design.
