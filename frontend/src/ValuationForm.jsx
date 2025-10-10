import React, { useEffect, useState } from "react";

const API_BASE = import.meta.env.VITE_API_BASE || "http://localhost:8080";

export default function ValuationForm() {
  const [formData, setFormData] = useState({
    ownerName: "",
    ownerMobile: "",
    carpetArea: "",
    possession: "",
    address: "",
  });

  const [savedId, setSavedId] = useState(() => {
    try { return localStorage.getItem("valuationSavedId") || null; } catch { return null; }
  });

  const [loading, setLoading] = useState(false);
  const [errorMsg, setErrorMsg] = useState(null);
  const [validationErrors, setValidationErrors] = useState({});

  // Toast state
  const [toast, setToast] = useState(null); // {message, type}

  useEffect(() => {
    try {
      if (savedId) localStorage.setItem("valuationSavedId", savedId);
      else localStorage.removeItem("valuationSavedId");
    } catch {}
  }, [savedId]);

  // show toast helper
  function showToast(message, type = "success", duration = 3800) {
    setToast({ message, type });
    setTimeout(() => {
      setToast(null);
    }, duration);
  }

  const handleChange = (e) => {
    setFormData((s) => ({ ...s, [e.target.name]: e.target.value }));
    setValidationErrors((v) => {
      const copy = { ...v }; delete copy[e.target.name]; return copy;
    });
    setErrorMsg(null);
  };

  const validateClient = () => {
    const errors = {};
    if (!formData.ownerName || !String(formData.ownerName).trim()) {
      errors.ownerName = "Owner name is required";
    }
    if (formData.carpetArea !== "" && formData.carpetArea !== null) {
      const n = Number(String(formData.carpetArea).trim());
      if (isNaN(n)) errors.carpetArea = "Carpet area must be a number";
      else if (n < 0) errors.carpetArea = "Carpet area cannot be negative";
    }
    return errors;
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setErrorMsg(null); setValidationErrors({});

    const clientErrors = validateClient();
    if (Object.keys(clientErrors).length > 0) {
      setValidationErrors(clientErrors); return;
    }

    setLoading(true);
    try {
      const payload = {
        ...formData,
        carpetArea: formData.carpetArea === "" || formData.carpetArea === null
          ? null : Number(String(formData.carpetArea).trim()),
      };

      const res = await fetch(`${API_BASE}/api/valuations`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(payload),
      });

      if (res.ok) {
        const saved = await res.json();
        setSavedId(String(saved.id));
        setFormData({ ownerName: "", ownerMobile: "", carpetArea: "", possession: "", address: "" });
        setErrorMsg(null); setValidationErrors({});
        showToast("Valuation saved ✓", "success");
      } else if (res.status === 400) {
        let body;
        try { body = await res.json(); } catch { body = null; }
        if (body && typeof body === "object") {
          setValidationErrors(body);
          setErrorMsg("Please fix validation errors and resubmit.");
          showToast("Validation failed", "error");
        } else {
          const txt = await res.text();
          setErrorMsg(`Failed to save valuation: ${txt || res.statusText}`);
          showToast("Save failed", "error");
        }
      } else {
        const txt = await res.text();
        setErrorMsg(`Failed to save valuation: ${txt || res.statusText}`);
        showToast("Save failed", "error");
      }
    } catch (err) {
      console.error(err); setErrorMsg("Network error while saving valuation");
      showToast("Network error", "error");
    } finally {
      setLoading(false);
    }
  };

  const openPreview = () => {
    if (!savedId) return showToast("Save a valuation first", "error");
    window.open(`${API_BASE}/api/valuations/${savedId}/preview`, "_blank");
  };

  const downloadPdf = () => {
    if (!savedId) return showToast("Save a valuation first", "error");
    window.open(`${API_BASE}/api/valuations/${savedId}/pdf`, "_blank");
  };

  const row = "flex items-start gap-4";

  return (
    <>
      <div className="space-y-6">
        <form onSubmit={handleSubmit} noValidate className="space-y-4">
          {/* Owner Name */}
          <div className={row}>
            <label className="label-col">Owner Name:</label>
            <div className="flex-1">
              <input name="ownerName" className="input" placeholder="Enter owner name" value={formData.ownerName} onChange={handleChange} required />
              {validationErrors.ownerName && <div className="form-error">{validationErrors.ownerName}</div>}
            </div>
          </div>

          {/* Owner Mobile */}
          <div className={row}>
            <label className="label-col">Owner Mobile:</label>
            <div className="flex-1">
              <input name="ownerMobile" className="input" placeholder="Enter mobile number" value={formData.ownerMobile} onChange={handleChange} />
              {validationErrors.ownerMobile && <div className="form-error">{validationErrors.ownerMobile}</div>}
            </div>
          </div>

          {/* Address */}
          <div className={row}>
            <label className="label-col">Property Address:</label>
            <div className="flex-1">
              <textarea name="address" className="textarea" placeholder="Enter property address" value={formData.address} onChange={handleChange} />
              {validationErrors.address && <div className="form-error">{validationErrors.address}</div>}
            </div>
          </div>

          {/* Carpet Area */}
          <div className={row}>
            <label className="label-col">Carpet Area (sq.ft):</label>
            <div className="flex-1">
              <input name="carpetArea" className="input" type="number" step="0.01" min="0" placeholder="Enter area in sq.ft" value={formData.carpetArea} onChange={handleChange} />
              {validationErrors.carpetArea && <div className="form-error">{validationErrors.carpetArea}</div>}
            </div>
          </div>

          {/* Possession */}
          <div className={row}>
            <label className="label-col">Possession:</label>
            <div className="flex-1">
              <input name="possession" className="input" placeholder="Ready / Under Construction" value={formData.possession} onChange={handleChange} />
              {validationErrors.possession && <div className="form-error">{validationErrors.possession}</div>}
            </div>
          </div>

          {/* Submit */}
          <div className={row}>
            <div className="label-col" />
            <div className="flex-1">
              <button type="submit" className="btn btn-primary w-full" disabled={loading}>{loading ? "Saving..." : "Submit"}</button>
            </div>
          </div>
        </form>

        {/* After-save controls */}
        {savedId && (
          <div className="controls-row mt-4">
            <div className="saved-badge">Saved ID: {savedId}</div>
            <div style={{ marginLeft: "auto", display: "flex", gap: 8 }}>
              <button className="btn btn-ghost" onClick={openPreview}>Preview Report</button>
              <button className="btn btn-success" onClick={downloadPdf}>Download PDF</button>
            </div>
          </div>
        )}

        {errorMsg && <div className="form-error">{errorMsg}</div>}
      </div>

      {/* Toast container */}
      <div className="toast-root" aria-live="polite" aria-atomic="true">
        {toast && (
          <div className={`toast ${toast.type === "success" ? "toast-success" : ""}`}>
            <div>{toast.message}</div>
            <button className="toast-close" onClick={() => setToast(null)}>✕</button>
          </div>
        )}
      </div>
    </>
  );
}
