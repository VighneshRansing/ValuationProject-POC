import React from "react";
import ValuationForm from "./ValuationForm";

function App() {
  return (
    <main className="form-card" style={{ maxWidth: 980 }}>
      {/* responsive bank heading: full text on wide / abbreviated on constrained heights */}
      <div
        style={{
          display: "flex",
          justifyContent: "space-between",
          alignItems: "center",
          marginBottom: "0.5rem",
        }}
      >
        <h1 className="bank-title">🏦 National Bank – Mumbai Branch (Valuation v1)</h1>
      </div>

      <ValuationForm />
    </main>
  );
}

export default App;
