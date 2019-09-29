package org.quest.models

import com.fasterxml.jackson.annotation.JsonIgnore
import org.hibernate.annotations.LazyCollection
import org.hibernate.annotations.LazyCollectionOption
import javax.persistence.*
import javax.validation.constraints.Email
import javax.validation.constraints.NotNull

@Entity
@Table(name = "customer", uniqueConstraints = [UniqueConstraint(columnNames = ["email"])]
)
class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    val id: Long? = null

    @Column(name = "name", nullable = false)
    lateinit var name: String

    @Email
    @Column(nullable = false)
    lateinit var email: String

    @Column(name = "phone", nullable = true)
    var phone: String? = ""

    @Column(name = "balance")
    var balance: Int = 0

    @Column(nullable = false)
    var emailVerified: Boolean = false

    @Column(name = "password")
    @JsonIgnore
    var password: String? = null

    @NotNull
    @Enumerated(EnumType.STRING)
    lateinit var provider: AuthProvider

    var providerId: String? = null
}