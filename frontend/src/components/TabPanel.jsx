import React from 'react';

export default function TabPanel({ children, value, index, id, labelledBy }) {
  const hidden = value !== index;

  // keep TabPanel neutral so spacing is controlled by the tablist and rows
  return (
    <div
      role="tabpanel"
      hidden={hidden}
      id={id}
      aria-labelledby={labelledBy}
      className={`${hidden ? 'hidden' : ''}`}
    >
      {!hidden && children}
    </div>
  );
}
