import React from 'react'
import CvCreate_user from '../../components/resume/CvCreate_user'
import BtnShort from '../../components/main/BtnShort'
import './css/CvCreate_btn.css'
import BtnLong from '../../components/main/BtnLong'

const CvCreateContainer_user = () => {
  return (
    <div className='container main-content'>
        <CvCreate_user/>
        <div className='cvCreate-btn'>
          <BtnLong btnLongText={"이력서 등록"}/>
        </div>
    </div>
  )
}

export default CvCreateContainer_user