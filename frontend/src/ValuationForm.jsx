import React, { useState } from "react";

export default function ValuationForm() {
  const [formData, setFormData] = useState({
    ownerName: "",
    ownerMobile: "",
    carpetArea: "",
    possession: "",
    address: "",
  });

  const handleChange = (e) => {
    setFormData({
      ...formData,
      [e.target.name]: e.target.value,
    });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    try {
      const response = await fetch("http://localhost:8080/api/valuations", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
          ...formData,
          carpetArea: parseFloat(formData.carpetArea),
        }),
      });

      if (response.ok) {
        const saved = await response.json();
        alert("✅ Valuation saved! ID: " + saved.id);
        setFormData({
          ownerName: "",
          ownerMobile: "",
          carpetArea: "",
          possession: "",
          address: "",
        });
      } else {
        alert("❌ Failed to save valuation!");
      }
    } catch (err) {
      console.error(err);
      alert("⚠️ Error connecting to backend");
    }
  };

  return (
    <div className="max-w-lg mx-auto bg-white shadow-lg rounded-2xl p-8 mt-10">
      <h1 className="text-2xl font-bold text-center mb-6">
        🏠 Property Valuation Form
      </h1>

      <form onSubmit={handleSubmit} className="space-y-4">
        <input
          type="text"
          name="ownerName"
          placeholder="Owner Name"
          value={formData.ownerName}
          onChange={handleChange}
          className="w-full p-3 border rounded-lg focus:ring-2 focus:ring-blue-500"
          required
        />

        <input
          type="text"
          name="ownerMobile"
          placeholder="Owner Mobile"
          value={formData.ownerMobile}
          onChange={handleChange}
          className="w-full p-3 border rounded-lg focus:ring-2 focus:ring-blue-500"
        />

        <input
          type="number"
          name="carpetArea"
          placeholder="Carpet Area (sq ft)"
          value={formData.carpetArea}
          onChange={handleChange}
          className="w-full p-3 border rounded-lg focus:ring-2 focus:ring-blue-500"
        />

        <input
          type="text"
          name="possession"
          placeholder="Possession (Ready / Under Construction)"
          value={formData.possession}
          onChange={handleChange}
          className="w-full p-3 border rounded-lg focus:ring-2 focus:ring-blue-500"
        />

        <textarea
          name="address"
          placeholder="Property Address"
          value={formData.address}
          onChange={handleChange}
          className="w-full p-3 border rounded-lg focus:ring-2 focus:ring-blue-500"
          rows="3"
        />

        <button
          type="submit"
          className="w-full bg-blue-600 text-white py-3 rounded-lg hover:bg-blue-700 transition"
        >
          Submit
        </button>
      </form>
    </div>
  );
}
