import React from 'react';

const Card = ({ title, subtitle, children, className = '', actions }) => (
  <div className={`card ${className}`}>
    {(title || subtitle || actions) && (
      <div className="card__header">
        <div>
          {title && <h3 className="card__title">{title}</h3>}
          {subtitle && <p className="card__subtitle">{subtitle}</p>}
        </div>
        {actions && <div className="card__actions">{actions}</div>}
      </div>
    )}
    <div className="card__body">{children}</div>
  </div>
);

export default Card;
