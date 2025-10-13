import React from 'react';

export default function TabPanel({ children, value, index, id, labelledBy }) {
  const hidden = value !== index;

  return (
    <div
      role="tabpanel"
      hidden={hidden}
      id={id}
      aria-labelledby={labelledBy}
      className={`mt-6 ${hidden ? 'hidden' : ''}`}
    >
      {!hidden && children}
    </div>
  );
}