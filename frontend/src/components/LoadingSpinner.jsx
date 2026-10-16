import React from 'react';

const LoadingSpinner = ({ size = 'md', text }) => (
  <div className={`spinner spinner--${size}`}>
    <div className="spinner__circle" />
    {text && <span className="spinner__text">{text}</span>}
  </div>
);

export default LoadingSpinner;
