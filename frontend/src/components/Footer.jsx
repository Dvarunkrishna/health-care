import React from 'react';

const Footer = () => (
  <footer className="footer">
    <div className="container footer__inner">
      <p>© {new Date().getFullYear()} MediCare Healthcare Appointment System. All rights reserved.</p>
      <p className="footer__links">
        <span>Privacy</span>
        <span>Terms</span>
        <span>Contact</span>
      </p>
    </div>
  </footer>
);

export default Footer;
