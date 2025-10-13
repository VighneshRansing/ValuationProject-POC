import React from "react";
import ValuationForm from "./ValuationForm";

function App() {
  return (
    <main className="form-card" style={{ maxWidth: 900 }}>
      <div style={{ display: "flex", justifyContent: "space-between", alignItems: "center", marginBottom: "0.5rem" }}>
        <h1>🏦 National Bank – Mumbai Branch (Valuation v1)</h1>
      </div>
      <ValuationForm />
    </main>
  );
}

export default App;
