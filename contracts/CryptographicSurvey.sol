// SPDX-License-Identifier: GPL-3.0

pragma solidity >=0.7.0 <0.9.0;

/** 
 * @title EncryptedSurvey
 * @dev Implements Survey process along with crpytography
 */
contract CryptographicSurvey {
    
    struct Meta {
        bool status;    // survey opened or not
        uint32 reward;
        string name;    // unique name
        string PK;      // participants should encrypt message by this
        string questions;   // survey questions
    }

    struct Survey {
        address maker;
        uint256 metaNum;    // linked meta info
        uint256 endTime;    // when reached, only 1 participant answers and closed
        uint256 count;  // max participants
        string[] answers;   // participants's answers list
    }
    
    Meta[] metaList;
    
    mapping(string => Survey) surveys;
    
    function makeSurvey (
        string memory _PK,
        string memory _name,
        uint32 _period,
        uint32 _count,
        uint32 _reward,
        string memory _questions
        ) 
    public {
        require(
            _checkSurveyExistance(_name) == false,
            "ERROR: Target survey name already exists"
        );
        Meta memory _meta = Meta({  // make new meta
            name: _name,
            PK: _PK,
            status: true,   // opened state
            reward: _reward,
            questions: _questions
        });
        metaList.push(_meta);
        
        string[] memory _answers;   // empty answers list 
        
        Survey memory _survey = Survey({    // make new Survey
            metaNum: metaList.length - 1,   // point to last index
            maker: msg.sender,
            endTime: _period + block.timestamp,  // in seconds
            count: _count,
            answers: _answers
        });
        surveys[_name] = _survey;
    }

    function querySurveyMetaList () public view returns (Meta[] memory) {
        return metaList;
    }
    
    function querySurveyDetail (string memory _name) public view returns (Survey memory) {
        require(    // check existance
            _checkSurveyExistance(_name) == true,
            "ERROR: Target survey name does not exists"
        );
        require(    // only maker can query
            surveys[_name].maker == msg.sender,
            "ERROR: you are not owner of the survey"
        );
        return surveys[_name];
    }
    
    function participate (  // participant answers to survey
        string memory _name,
        string memory _encrypted    // answers encrypted by Survey's PK
        ) 
    public {
        _checkSurveyState(_name);   // survey is opened or not?
        surveys[_name].answers.push(    // append my encrypted answers
            _encrypted
        );
        _updateSurveyState(_name);  // close survey if time over or count over

    }
    
    function _checkSurveyState (string memory _name) private view {
        require(    // check existance
            _checkSurveyExistance(_name) == true,
            "ERROR: Target survey name does not exists"
        );
        require(    // check opened or not
            metaList[surveys[_name].metaNum].status == true,
            "ERROR: Target survey already closed"
        );
    }
    
    function _updateSurveyState (string memory _name) private {
        require(    // check existance
            _checkSurveyExistance(_name) == true,
            "ERROR: Target survey name does not exists"
        );
        Survey memory _survey = surveys[_name];
        Meta storage _meta = metaList[_survey.metaNum];
        if (_survey.answers.length >= _survey.count) {  // when count over
            _meta.status = false;   // close survey
        }

        if (_survey.endTime < block.timestamp) {    // when time over
            _meta.status = false;   // close survey
        }
    }
    
    function _checkSurveyExistance (string memory _name) private view returns (bool) {
        return surveys[_name].maker != address(0);  // if not initialized, all values are 0 by default
    }
}