package audio.security.oauth2

import audio.exception.OAuth2AuthenticationProcessingException
import audio.models.AuthProvider
import audio.models.Customer
import audio.repositories.CustomerRepository
import audio.security.UserPrincipal
import audio.security.oauth2.user.OAuth2UserInfo
import audio.security.oauth2.user.OAuth2UserInfoFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.authentication.InternalAuthenticationServiceException
import org.springframework.security.core.AuthenticationException
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest
import org.springframework.security.oauth2.core.OAuth2AuthenticationException
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.stereotype.Service
import org.springframework.util.StringUtils

@Service
class CustomOAuth2UserService : DefaultOAuth2UserService() {

    @Autowired
    private val customerRepository: CustomerRepository? = null

    @Throws(OAuth2AuthenticationException::class)
    override fun loadUser(oAuth2UserRequest: OAuth2UserRequest): OAuth2User {
        val oAuth2User = super.loadUser(oAuth2UserRequest)

        try {
            return processOAuth2User(oAuth2UserRequest, oAuth2User)
        } catch (ex: AuthenticationException) {
            throw ex
        } catch (ex: Exception) {
            // Throwing an instance of AuthenticationException will trigger the OAuth2AuthenticationFailureHandler
            throw InternalAuthenticationServiceException(ex.message, ex.cause)
        }

    }

    private fun processOAuth2User(oAuth2UserRequest: OAuth2UserRequest, oAuth2User: OAuth2User): OAuth2User {
        val oAuth2UserInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(
                oAuth2UserRequest.clientRegistration.registrationId, oAuth2User.attributes)
        if (StringUtils.isEmpty(oAuth2UserInfo!!.getEmail())) {

            throw OAuth2AuthenticationProcessingException("Email not found from OAuth2 provider")
        }

        val userOptional = customerRepository!!.findByEmail(oAuth2UserInfo.getEmail())
        var customer: Customer
        if (userOptional.isPresent) {
            customer = userOptional.get()
            if (customer.provider != AuthProvider.valueOf(oAuth2UserRequest.clientRegistration.registrationId)) {
                throw OAuth2AuthenticationProcessingException("Looks like you're signed up with " +
                        customer.provider + " account. Please use your " + customer.provider +
                        " account to login.")
            }
            customer = updateExistingUser(customer, oAuth2UserInfo)
        } else {
            customer = registerNewUser(oAuth2UserRequest, oAuth2UserInfo)
        }

        return UserPrincipal.create(customer, oAuth2User.attributes)
    }

    private fun registerNewUser(oAuth2UserRequest: OAuth2UserRequest, oAuth2UserInfo: OAuth2UserInfo): Customer {
        val user = Customer()
        user.provider = AuthProvider.valueOf(oAuth2UserRequest.clientRegistration.registrationId)
        user.providerId = oAuth2UserInfo.getId()
        user.name = oAuth2UserInfo.getName()
        user.email = oAuth2UserInfo.getEmail()
        return customerRepository!!.save(user)
    }

    private fun updateExistingUser(existingCustomer: Customer, oAuth2UserInfo: OAuth2UserInfo): Customer {
        existingCustomer.name = oAuth2UserInfo.getName()
        return customerRepository!!.save<Customer>(existingCustomer)
    }
}