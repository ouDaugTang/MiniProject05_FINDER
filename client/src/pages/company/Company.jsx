import React from 'react'
import Header from '../../layouts/Header'
import Footer from '../../layouts/Footer'
import ComDetailContainer from '../../containers/company/ComDetailContainer'
import CreditCom from '../../components/company/CreditCom'


const Company = () => {
  return (
    <>
      <Header />
      {/* <ComDetailContainer /> */}
      <CreditCom />
      <Footer />
    </>
  )
}

export default Company